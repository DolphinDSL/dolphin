package pt.lsts.nvl.util.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import pt.lsts.nvl.runtime.NVLExecutionException;

public final class NetworkInterfaces {

   
  public static Collection<InetAddress> get(boolean includeLoopback) {
    ArrayList<InetAddress> itfs = new ArrayList<>();
    try {
  
      Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
      while (nis.hasMoreElements()) {
        NetworkInterface ni = nis.nextElement();
        try {
          if (ni.isLoopback() && !includeLoopback)
            continue;
        }
        catch (Exception e) {
          continue;
        }

        Enumeration<InetAddress> adrs = ni.getInetAddresses();
        while (adrs.hasMoreElements()) {
          InetAddress addr = adrs.nextElement();
          if (addr instanceof Inet4Address)
            itfs.add(addr);
        }
      }
    }
    catch (Exception e) {
      throw new NVLExecutionException(e);
    }
    return itfs;
  }
  
  private NetworkInterfaces() { }
  
}
