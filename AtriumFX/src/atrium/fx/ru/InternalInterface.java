package atrium.fx.ru;

import atrium.fx.ru.core.ClientApplication;
import atrium.fx.ru.core.ModalWindow;
import atrium.fx.ru.core.UITemplate;
import javafx.animation.AnimationTimer;

/**
 * Created by Andrey.A.Koshkin on 24.09.2015.
 */
public class InternalInterface {

    public static  ClientApplication clientApplication;

    public static  ModalWindow modalWindow;

    public static boolean modalWindowNeedToClose = true;
    private  static UITemplate delayedWindow;

    // метод устанавливает форму для отображения
    public static void showWindow(UITemplate ui) {
        InternalInterface.modalWindowNeedToClose = false;
        ui.start();
    }

    //метод полготавливает систему к установке новой формы для отображдения
    // позволяет управлять формами из других потоков
    public static void prepareDelayedSHow(UITemplate ui)
    {
        InternalInterface.delayedWindow = ui;

        InternalInterface.modalWindowNeedToClose = false;

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (InternalInterface.modalWindowNeedToClose){
                    InternalInterface.delayedWindow.start();
                    this.stop();
                }
            }
        };

        timer.start();
    }

    // метод устанавливает отложенную форму как активную. Может вызываться из других потоков
    public static void showDelayedWindow()
    {
        InternalInterface.modalWindowNeedToClose = true;
    }
}
