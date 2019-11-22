package pt.lsts.dolphin.runtime.mavlink;

import java.io.File;

import pt.lsts.dolphin.dsl.Engine;
import pt.lsts.dolphin.util.Debug;

/**
 * Program to interpret Dolphin files with the MAVLink runtime.
 */
public final class Main {

    /**
     * Main method.
     *
     * @param args Program arguments.
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Please specify the script(s) to run!");
            return;
        }

        MAVLinkCommunications.getInstance().start();

//        Debug.enable(System.out, false);

        try {
            Engine engine = Engine.create(new MAVLinkPlatform());

            for (String fileName : args) {
                engine.run(new File(fileName));
            }

            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            MAVLinkCommunications.getInstance().terminate();
        }
    }

    /**
     * Prevent instantiation.
     */
    private Main() {

    }
}
