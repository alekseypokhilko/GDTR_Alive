package org.happysanta.gdtralive.game.visual;

import org.happysanta.gdtralive.game.Constants;

public class SplashScreen {
    private int m_longI = 0;

    public void showSplashScreens(GdView gdView) {
        long l2;
        long imageDelay = Constants.IMAGES_DELAY;
        for (; imageDelay > 0L; imageDelay -= l2)
            l2 = _avJ();

        imageDelay = Constants.IMAGES_DELAY;
        gdView.setLoadingState(LoadingState.SPLASH2);
        long l3;
        for (long l4 = imageDelay; l4 > 0L; l4 -= l3)
            l3 = _avJ();

        while (m_longI < 10)
            _avJ();
        gdView.setLoadingState(null);
    }

    private long _avJ() {
        m_longI++;
        long l = System.currentTimeMillis();
        if (m_longI < 1 || m_longI > 10) { // maybe < 1 not needed?
            m_longI--;
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignored) {
            }
        }
        return System.currentTimeMillis() - l;
    }
}
