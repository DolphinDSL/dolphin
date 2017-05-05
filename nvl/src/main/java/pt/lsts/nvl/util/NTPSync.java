package pt.lsts.nvl.util;

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;


public final class NTPSync {

  private static final int  ORIGINATE_TIME_OFFSET = 24;
  private static final int  RECEIVE_TIME_OFFSET   = 32;
  private static final int  TRANSMIT_TIME_OFFSET  = 40;
  private static final int  NTP_PACKET_SIZE       = 48;
  private static final int  NTP_PORT              = 123;
  private static final int  NTP_MODE_CLIENT       = 3;
  private static final int  NTP_MODE_SERVER       = 4;
  private static final int  NTP_MODE_BROADCAST    = 5;
  private static final int  NTP_VERSION           = 3;
  private static final int  NTP_LEAP_NOSYNC       = 3;
  private static final int  NTP_STRATUM_DEATH     = 0;
  private static final int  NTP_STRATUM_MAX       = 15;
  private static final int  SOCKET_TIMEOUT = 1000;

  // Number of seconds between Jan 1, 1900 and Jan 1, 1970
  // 70 years plus 17 leap days
  private static final long OFFSET_1900_TO_1970   = ((365L * 70L) + 17L) * 24L
      * 60L * 60L;


  @SuppressWarnings("serial")
  public static class NTPSynchronizationException extends RuntimeException {
    public NTPSynchronizationException(String message) {
      super(message);
    }
    public NTPSynchronizationException(String message, Throwable cause) {
      super(message, cause);
    }
  }


  public static long getDelta(String host) {
    try {
      return getDelta(InetAddress.getByName(host));
    } catch (UnknownHostException e) {
      throw new NTPSynchronizationException("Host not known", e);
    }
  }

  public static long getDelta(InetAddress address) {
    DatagramSocket socket = null;
    try {
      socket = new DatagramSocket();
      socket.setSoTimeout(SOCKET_TIMEOUT);
      byte[] buffer = new byte[NTP_PACKET_SIZE];
      DatagramPacket request = new DatagramPacket(buffer, buffer.length,
          address, NTP_PORT);
      // set mode = 3 (client) and version = 3
      // mode is in low 3 bits of first byte
      // version is in bits 3-5 of first byte
      buffer[0] = NTP_MODE_CLIENT | (NTP_VERSION << 3);
      // get current time and write it to the request packet
      final long requestTime = System.currentTimeMillis();
      final long requestTicks = System.currentTimeMillis();
      writeTimeStamp(buffer, TRANSMIT_TIME_OFFSET, requestTime);
      socket.send(request);
      // read the response
      DatagramPacket response = new DatagramPacket(buffer, buffer.length);
      socket.receive(response);
      final long responseTicks = System.currentTimeMillis();
      final long responseTime = requestTime + (responseTicks - requestTicks);
      // extract the results
      final byte leap = (byte) ((buffer[0] >> 6) & 0x3);
      final byte mode = (byte) (buffer[0] & 0x7);
      final int stratum = (int) (buffer[1] & 0xff);
      final long originateTime = readTimeStamp(buffer, ORIGINATE_TIME_OFFSET);
      final long receiveTime = readTimeStamp(buffer, RECEIVE_TIME_OFFSET);
      final long transmitTime = readTimeStamp(buffer, TRANSMIT_TIME_OFFSET);
      /* do sanity check according to RFC */
      // TODO: validate originateTime == requestTime.
      checkValidServerReply(leap, mode, stratum, transmitTime);
      // long roundTripTime = responseTicks - requestTicks - (transmitTime -
      // receiveTime);
      // receiveTime = originateTime + transit + skew
      // responseTime = transmitTime + transit - skew
      // clockOffset = ((receiveTime - originateTime) + (transmitTime -
      // responseTime))/2
      // = ((originateTime + transit + skew - originateTime) +
      // (transmitTime - (transmitTime + transit - skew)))/2
      // = ((transit + skew) + (transmitTime - transmitTime - transit + skew))/2
      // = (transit + skew - transit + skew)/2
      // = (2 * skew)/2 = skew
      long clockOffset = ((receiveTime - originateTime)
          + (transmitTime - responseTime)) / 2;
      // EventLogTags.writeNtpSuccess(address.toString(), roundTripTime,
      // clockOffset);
      // if (DBG) {
      // KVS_CA.Log.d(TAG, "round trip: " + roundTripTime + "ms, " +
      // "clock offset: " + clockOffset + "ms");
      // }
      // save our results - use the times on this side of the network latency
      // (response rather than request time)
      long mNtpTime = responseTime + clockOffset;
      long mNtpTimeReference = responseTicks;
      // long mRoundTripTime = roundTripTime;
      return mNtpTime - mNtpTimeReference;
    } 
    catch (IOException e) {
      throw new NTPSynchronizationException("I/O error", e);
    } 
    finally {
      if (socket != null) {
        socket.close();
      }
    }
  }

  private static void checkValidServerReply(byte leap, byte mode, int stratum,
      long transmitTime) throws NTPSynchronizationException {
    if (leap == NTP_LEAP_NOSYNC) {
      throw new NTPSynchronizationException("unsynchronized server");
    }
    if ((mode != NTP_MODE_SERVER) && (mode != NTP_MODE_BROADCAST)) {
      throw new NTPSynchronizationException("untrusted mode: " + mode);
    }
    if ((stratum == NTP_STRATUM_DEATH) || (stratum > NTP_STRATUM_MAX)) {
      throw new NTPSynchronizationException("untrusted stratum: " + stratum);
    }
    if (transmitTime == 0) {
      throw new NTPSynchronizationException("zero transmitTime");
    }
  }

  /**
   * Reads an unsigned 32 bit big endian number from the given offset in the
   * buffer.
   */
  private static long read32(byte[] buffer, int offset) {
    byte b0 = buffer[offset];
    byte b1 = buffer[offset + 1];
    byte b2 = buffer[offset + 2];
    byte b3 = buffer[offset + 3];
    // convert signed bytes to unsigned values
    int i0 = ((b0 & 0x80) == 0x80 ? (b0 & 0x7F) + 0x80 : b0);
    int i1 = ((b1 & 0x80) == 0x80 ? (b1 & 0x7F) + 0x80 : b1);
    int i2 = ((b2 & 0x80) == 0x80 ? (b2 & 0x7F) + 0x80 : b2);
    int i3 = ((b3 & 0x80) == 0x80 ? (b3 & 0x7F) + 0x80 : b3);
    return ((long) i0 << 24) + ((long) i1 << 16) + ((long) i2 << 8) + (long) i3;
  }

  /**
   * Reads the NTP time stamp at the given offset in the buffer and returns it
   * as a system time (milliseconds since January 1, 1970).
   */
  private static long readTimeStamp(byte[] buffer, int offset) {
    long seconds = read32(buffer, offset);
    long fraction = read32(buffer, offset + 4);
    // Special case: zero means zero.
    if (seconds == 0 && fraction == 0) {
      return 0;
    }
    return ((seconds - OFFSET_1900_TO_1970) * 1000)
        + ((fraction * 1000L) / 0x100000000L);
  }

  /**
   * Writes system time (milliseconds since January 1, 1970) as an NTP time
   * stamp at the given offset in the buffer.
   */
  private static void writeTimeStamp(byte[] buffer, int offset, long time) {
    // Special case: zero means zero.
    if (time == 0) {
      Arrays.fill(buffer, offset, offset + 8, (byte) 0x00);
      return;
    }
    long seconds = time / 1000L;
    long milliseconds = time - seconds * 1000L;
    seconds += OFFSET_1900_TO_1970;
    // write seconds in big endian format
    buffer[offset++] = (byte) (seconds >> 24);
    buffer[offset++] = (byte) (seconds >> 16);
    buffer[offset++] = (byte) (seconds >> 8);
    buffer[offset++] = (byte) (seconds >> 0);
    long fraction = milliseconds * 0x100000000L / 1000L;
    // write fraction in big endian format
    buffer[offset++] = (byte) (fraction >> 24);
    buffer[offset++] = (byte) (fraction >> 16);
    buffer[offset++] = (byte) (fraction >> 8);
    // low order bits should be random data
    buffer[offset++] = (byte) (Math.random() * 255.0);
  }

  private NTPSync() {

  }
}
