package display;

public class ViewFrameFactory {
    public static ViewFrame create()
    {
        return new ViewFrameImpl();
    }
}
