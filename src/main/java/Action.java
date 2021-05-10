import entity.Category;
import entity.Option;
import entity.Product;
import entity.Value;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class Action {

    private static final EntityManagerFactory FACTORY;

    static {
        FACTORY = Persistence.createEntityManagerFactory("default");
    }

    private static final Scanner IN = new Scanner(System.in);

    public static void menu() {
        System.out.println("""
                Создать товар[1]
                Редактровать товар[2]
                Удалить товар[3]""");
        System.out.print("Выберите что нужно: ");
        String action = IN.nextLine();
        switch (action) {
            case "1" -> create();
            case "2" -> update();
            case "3" -> remove();
            default -> System.out.println("Такого действя не существует");
        }
    }

    public static void create() {
        EntityManager manager = FACTORY.createEntityManager();
        manager.getTransaction().begin();
        List<Category> categories = manager
                .createQuery("""
                        select c
                        from Category c
                        order by c.id
                        """, Category.class)
                .getResultList();
        for (Category category : categories) {
            System.out.println(category.getName() + " [" + category.getId() + "]");
        }
        System.out.print("Введите ID категории: ");
        long categoryId = Long.parseLong(IN.nextLine());
        System.out.print("Введите название товара: ");
        String productName = IN.nextLine();
        System.out.print("Введите описание товара: ");
        String productDescription = IN.nextLine();
        System.out.print("Введите цену товара: ");
        String productPrice = IN.nextLine();
        Product product = new Product();
        product.setCategory(manager.find(Category.class, categoryId));
        product.setName(productName);
        product.setPrice(Double.parseDouble(productPrice));
        product.setDescription(productDescription);
        manager.persist(product);
        List<Option> options = product.getCategory().getOptions();
        for (Option option : options) {
            System.out.print(option.getName() + ": ");
            String inValue = IN.nextLine();
            Value value = new Value();
            value.setValue(inValue);
            value.setProduct(product);
            value.setOption(option);
            manager.persist(value);
        }
        manager.getTransaction().commit();
        manager.close();
    }

    public static void update() {
        EntityManager manager = FACTORY.createEntityManager();
        manager.getTransaction().begin();
        System.out.print("Введите ID товара: ");
        long productId = Long.parseLong(IN.nextLine());
        Product product = manager.find(Product.class, productId);
        System.out.print("Введите название товара [" + product.getName() + "]: ");
        String productName = IN.nextLine();
        if (!productName.isEmpty()) {
            product.setName(productName);
        }
        System.out.print("Введите описание товара [" + product.getDescription() + "]: ");
        String productDescription = IN.nextLine();
        if (!productDescription.isEmpty()) {
            product.setDescription(productDescription);
        }
        System.out.print("Введите цену товара [" + product.getPrice() + "]: ");
        String productPrice = IN.nextLine();
        if (!productPrice.isEmpty() && productPrice.matches("^\\d+(\\.\\d+)?$")) {
            product.setPrice(Double.parseDouble(productPrice));
        }
        List<Option> options = product.getCategory().getOptions();
        for (Option option : options) {
            List<Value> values = manager
                    .createQuery("""
                            select v
                            from Value v
                            where v.product = :product and
                                  v.option = :option
                            """, Value.class)
                    .setParameter("product", product)
                    .setParameter("option", option)
                    .setMaxResults(1)
                    .getResultList();
            if (!values.isEmpty()) {
                Value value = values.get(0);
                PrintStream var10000 = System.out;
                var10000.print(option.getName() + " [" + value.getValue() + "]: ");
                String input = IN.nextLine();
                if (!input.isEmpty()) {
                    value.setValue(input);
                }
            } else {
                System.out.print(option.getName() + ": ");
                String inValue = IN.nextLine();
                Value value = new Value();
                value.setValue(inValue);
                value.setProduct(product);
                value.setOption(option);
                manager.persist(value);
            }
        }
        manager.getTransaction().commit();
        manager.close();
    }

    public static void remove() {
        EntityManager manager = FACTORY.createEntityManager();
        manager.getTransaction().begin();
        System.out.print("Введите ID товара: ");
        Long productId = Long.parseLong(IN.nextLine());
        Product product = manager.find(Product.class, productId);
        manager.remove(product);
        manager.getTransaction().commit();
        manager.close();
    }
}
