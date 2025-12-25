package dss2526.service.gestao;

import dss2526.service.base.BaseFacade;

public class GestaoFacade extends BaseFacade implements IGestaoFacade {

    private static GestaoFacade instance;

    private GestaoFacade() {}

    public static synchronized GestaoFacade getInstance() {
        if (instance == null) {
            instance = new GestaoFacade();
        }
        return instance;
    }
}