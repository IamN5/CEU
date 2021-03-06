package framework.core.ui;

import app.product.Product;
import app.user.User;
import framework.core.db.DatabaseInterface;
import framework.Logger;
import screens.Screen;
import screens.stocks.StockForm;
import screens.stocks.Stocks;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class JFrameManager {
    private JFrame frame;
    private JDialog modal;
    private HashMap<String, DatabaseInterface<?> > interfaces;
    private User loggedUser;

    public void load(Screen pane) {
        load(pane, true);
    }

    public void load(Screen pane, boolean centerFrame) {
        load(pane, centerFrame, 0, 0);
    }

    public void load(Screen pane, boolean centerFrame, int width, int height) {
        if (frame != null) frame.dispose();

        frame = new JFrame(pane.getTitle());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(pane);
        frame.pack();
        frame.setResizable(false);
        frame.setIconImages(getIconList());
        frame.setVisible(true);

        if (width != 0 && height != 0) frame.setSize(width, height);
        if (centerFrame) centerFrame();

        Logger.info("Tela " + pane.getClass().getSimpleName() + " carregada");
    }

    public void loadModal(Class<? extends JDialog> dialogClass, String title, Stocks stockScreen, Product product, int stockId) {
        loadModal(dialogClass, title, true, 0, 0, stockScreen, product, stockId);
    }

    public void loadModal(Class<? extends JDialog> dialogClass, String title, Stocks stockScreen, int stockId) {
        loadModal(dialogClass, title, true, stockScreen, stockId);
    }

    public void loadModal(Class<? extends JDialog> dialogClass, String title, boolean centerModal, Stocks stockScreen, int stockId) {
        loadModal(dialogClass, title, centerModal, 0, 0, stockScreen, null, stockId);
    }

    public void loadModal(
            Class<? extends JDialog> dialogClass,
            String title,
            boolean centerModal,
            int width,
            int height,
            Stocks stockScreen,
            Product product,
            int stockId)
    {
        if (modal != null) modal.dispose();

        try {
            modal = dialogClass.getDeclaredConstructor(Window.class, Stocks.class).newInstance(frame, stockScreen);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Logger.error("Error while loading modal " + dialogClass.getSimpleName());
            e.printStackTrace();

            return;
        }

        StockForm stockForm = (StockForm) modal;
        stockForm.setStockId(stockId);

        if (product != null) {


            stockForm.setNameField(product.getName());
            stockForm.setAmountField(product.getAmount());
            stockForm.setProductId(product.getId());

            Logger.info("Set modal field \"Name\" to " + product.getName());
            Logger.info("Set modal field \"Amount\" to " + product.getAmount());
        }

        modal.setTitle(title);
        modal.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        modal.pack();
        modal.setResizable(false);
        modal.setIconImages(getIconList());
        modal.setVisible(true);

        if (width != 0 && height != 0) modal.setSize(width, height);
        if (centerModal) centerModal();

        Logger.info("Loaded modal " + modal.getClass().getSimpleName());
    }

    private ArrayList<Image> getIconList() {
        ArrayList<Image> imageList = new ArrayList<>();

        URL cloud20Url = getClass().getResource("/resources/ceu-20x12.svg");
        URL cloud40Url = getClass().getResource("/resources/ceu-40x24.svg");
        URL cloud50Url = getClass().getResource("/resources/ceu-50x30.svg");

        BufferedImage img20 = null;
        BufferedImage img40 = null;
        BufferedImage img50 = null;
        try {
            img20 = ImageIO.read(cloud20Url);
            img40 = ImageIO.read(cloud40Url);
            img50 = ImageIO.read(cloud50Url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageList.add(img20);
        imageList.add(img40);
        imageList.add(img50);

        return imageList;
    }

    private void center(Window window) {
        Dimension dimensions = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation((dimensions.width - window.getWidth()) / 2, (dimensions.height - window.getHeight()) / 2);
    }

    public void setTitle(String title) {
        frame.setTitle(title);
    }

    public void setLoggedUser(User user) {
        loggedUser = user;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void centerFrame() {
        center(frame);
    }

    public void centerModal() {
        center(modal);
    }

    public JFrame getFrame() {
        return frame;
    }

    public JDialog getModal() {
        return modal;
    }

    public DatabaseInterface<?> getInterface(String name) {
        return interfaces.get(name);
    }

    public void setInterfaces(HashMap<String, DatabaseInterface<?>> interfaces) {
        this.interfaces = interfaces;
    }
}
