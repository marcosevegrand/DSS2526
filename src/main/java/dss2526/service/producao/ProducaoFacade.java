package dss2526.service.producao;

import dss2526.service.base.BaseFacade;

public class ProducaoFacade extends BaseFacade implements IProducaoFacade {

    private static ProducaoFacade instance;

    private ProducaoFacade() {
    }

    public static synchronized ProducaoFacade getInstance() {
        if (instance == null) {
            instance = new ProducaoFacade();
        }
        return instance;
    }
}