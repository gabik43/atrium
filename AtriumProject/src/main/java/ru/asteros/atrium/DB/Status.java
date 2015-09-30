package ru.asteros.atrium.DB;

/**
 * Created by Timofey Boldyrev on 30.09.2015.
 */
public enum Status {

    BLOCKED {
        public String translate() {
            return "Подготовка к работе";
        }
    },

    RESERVED {
        public String translate() {
            return "Подготовка к работе";
        }
    },

    NEW {
        public String translate() {
            return "Новый";
        }
    },

    READY {
        public String translate() {
            return "В работе";
        }
    },

    PERFORMED {
        public String translate() {
            return "В работе";
        }
    },

    DONE {
        public String translate() {
            return "Выполнено";
        }
    },

    ERROR {
        public String translate() {
            return "Ошибка";
        }
    };

    public String translate() {
        return null;
    }

}
