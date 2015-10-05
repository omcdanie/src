package display;

public interface ViewFrame {

    int getXSize();

    int getYSize();

    int getZSize();

    void stop();

    boolean isStopped();

    void updateItem(ConsoleItem ci);

    void removeItem(String id);

    int numItems();

    void clearAllItems();

    void updateInfo(String s);

    void toggleGrid();

    void toggleStars();
}
