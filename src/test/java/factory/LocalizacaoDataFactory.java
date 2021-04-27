package factory;

import pojo.Local;

public class LocalizacaoDataFactory {

    public static Local criarLocalizacao(){

        Local local = new Local();
        local.setCep("13730-000");
        local.setCidade("Mococa");
        local.setLatitude("-21.4661241");
        local.setLongitude("-47.0095989");
        local.setPais("BR");
        return  local;
    }

}
