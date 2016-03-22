# Introduction #

This will get you started. You will have to check some requirements:

  * A MySQL installation for the L2JSever
  * A running L2JServer (code, setup & installation from http://www.l2jserver.com)
  * A working installation of the jmonkeyengine (code, setup & installation from http://www.jmonkeyengine.org)
  * Some additional files from http://www.lwjgl.org


# Details #

Head over to l2jserver.com and install an l2jserver. Once you are done and have tested it perhaps with the official client you can go to the next step.

Depending on what your favorite development IDE is you will try the whole thing on NetBeans, or on Eclipse. The code here in the repository is

Jmonkeyengine works out of the box with Netbeans, in Eclipse you need to do some more work. Checkout the engine, change the .classpath file (Eclipse only), apply the current patch for the jme engine from the FCClient root directory, add lwjgl\_util.jar into the lib directory.

Checkout FCClient, FCData, FCOgre and SwingGui, should workout of the box in Eclipse, on Netbeans I hope you know what you are doing.

Head over to FCClient/README.txt this should be the last steps. Then start FCClient/src/com/l2client/app/L2JClient