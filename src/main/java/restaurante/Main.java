package restaurante;

import restaurante.business.IRestauranteFacade;
import restaurante.business.RestauranteFacade;
import restaurante.data.IRestauranteDAO;
import restaurante.data.RestauranteDAO;
import restaurante.ui.text.TextUI;

public class Main {
    public static void main(String[] args) {
        System.out.println("Sistema de Gestão de Restaurantes - Fast Food Chain");
        System.out.println("====================================================");

        // 1) criar camada de dados (DAO ligado à BD)
        IRestauranteDAO dao = new RestauranteDAO();

        // 2) criar fachada de negócios (implementa IRestauranteFacade)
        IRestauranteFacade facade = new RestauranteFacade(dao);

        // 3) criar UI de texto e arrancar
        TextUI ui = new TextUI(facade);
        ui.iniciar();
    }
}
