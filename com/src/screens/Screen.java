package screens;

import core.ui.JFrameManager;

import javax.swing.*;

public class Screen extends JPanel {
    private final JFrameManager frameManager;
    private final String title;

    public Screen(String title, JFrameManager frameManager) {
        this.title = title;
        this.frameManager = frameManager;
    }

    public String getTitle() {
        return title;
    }

    public JFrameManager getFrameManager() {
        return frameManager;
    }
}
