package atrium.fx.ru;

import atrium.fx.ru.core.ClientApplication;
import atrium.fx.ru.core.ModalWindow;
import atrium.fx.ru.core.UITemplate;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;

/**
 * Created by Andrey.A.Koshkin on 24.09.2015.
 */
public class InternalInterface {

    public static  ClientApplication clientApplication;

    public static  ModalWindow modalWindow;

    public static boolean needToChangeWindow = true;
    private  static UITemplate delayedWindow;
    public static Thread waitThread;

    // метод устанавливает форму для отображения
    public static void showWindow(UITemplate ui) {
        InternalInterface.needToChangeWindow = false;
        ui.show();
    }

    public static void setDelayedWindow(UITemplate ui)
    {
        InternalInterface.delayedWindow = ui;
    }

    //метод полготавливает систему к установке новой формы для отображдения
    // позволяет управлять формами из других потоков
    public static void prepareDelayedSHow(UITemplate ui)
    {
        InternalInterface.delayedWindow = ui;

        InternalInterface.needToChangeWindow = false;

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (InternalInterface.needToChangeWindow){
                    InternalInterface.delayedWindow.show();
                    this.stop();
                }
            }
        };

        timer.start();
    }

    
    public static void prepareDelayedSHow(UITemplate ui, Thread thread)
    {
        waitThread = thread;
        InternalInterface.delayedWindow = ui;

        InternalInterface.needToChangeWindow = false;

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (!InternalInterface.waitThread.isAlive()){
                    InternalInterface.delayedWindow.show();
                    this.stop();
                }
            }
        };

        timer.start();
    }

    // метод устанавливает отложенную форму как активную. Может вызываться из других потоков
    public static void showDelayedWindow()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                InternalInterface.needToChangeWindow = true;
            }
        });
    }
}
