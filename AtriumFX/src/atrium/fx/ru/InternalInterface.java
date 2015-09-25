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

    // ����� ������������� ����� ��� �����������
    public static void showWindow(UITemplate ui) {
        InternalInterface.modalWindowNeedToClose = false;
        ui.start();
    }

    //����� �������������� ������� � ��������� ����� ����� ��� ������������
    // ��������� ��������� ������� �� ������ �������
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

    // ����� ������������� ���������� ����� ��� ��������. ����� ���������� �� ������ �������
    public static void showDelayedWindow()
    {
        InternalInterface.modalWindowNeedToClose = true;
    }
}
