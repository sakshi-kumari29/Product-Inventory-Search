import java.util.*;
import java.io.*;

// Product Class
class Product {
    private String productId;
    private String name;
    private int quantity;
    private double price;

    Product(String productId, String name, int quantity, double price) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void display() {
        System.out.println("ID: " + productId +
                " | Name: " + name +
                " | Quantity: " + quantity +
                " | Price: " + price);
    }
}

// Inventory Class
class Inventory {

    ArrayList<Product> products = new ArrayList<>();
    HashMap<String, Product> productMap = new HashMap<>();

    // Add Product
    public void addProduct(Product p) {

        String id = p.getProductId().toUpperCase();

        if (productMap.containsKey(id)) {
            System.out.println("Product ID already exists!");
            return;
        }

        products.add(p);
        productMap.put(id, p);

        System.out.println("Product added successfully!");
    }

    // Display All Products
    public void displayAll() {

        if (products.isEmpty()) {
            System.out.println("Inventory is empty!");
            return;
        }

        System.out.println("\n----- PRODUCT INVENTORY -----");

        for (Product p : products) {
            p.display();
        }
    }

    // Search By Product ID using HashMap
    public void searchById(String id) {

        Product p = productMap.get(id.toUpperCase());

        if (p != null) {
            System.out.println("\nProduct Found:");
            p.display();
        } else {
            System.out.println("Product ID " + id + " not found.");
        }
    }

    // Search By Product Name using Binary Search
    public void searchByName(String name) {

        ArrayList<Product> sortedList = new ArrayList<>(products);

        sortedList.sort(
            Comparator.comparing(Product::getName,
            String.CASE_INSENSITIVE_ORDER)
        );

        int low = 0;
        int high = sortedList.size() - 1;

        while (low <= high) {

            int mid = (low + high) / 2;

            String currentName = sortedList.get(mid).getName();

            int result = currentName.compareToIgnoreCase(name);

            if (result == 0) {
                System.out.println("\nProduct Found:");
                sortedList.get(mid).display();
                return;
            }

            else if (result < 0) {
                low = mid + 1;
            }

            else {
                high = mid - 1;
            }
        }

        System.out.println("Product " + name + " not found.");
    }
}

// FileHandler Class
class FileHandler {

    static final String FILE_NAME = "products.txt";

    // Save Products
    public static void saveToFile(Inventory inventory) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {

            for (Product p : inventory.products) {

                writer.println(
                    p.getProductId() + "," +
                    p.getName() + "," +
                    p.getQuantity() + "," +
                    p.getPrice()
                );
            }

        } catch (IOException e) {
            System.out.println("Error saving file!");
        }
    }

    // Load Products
    public static void loadFromFile(Inventory inventory) {

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
             new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                try {
                    String[] data = line.split(",");

                    if (data.length != 4) {
                        continue;
                    }

                    String id = data[0];
                    String name = data[1];
                    int quantity = Integer.parseInt(data[2]);
                    double price = Double.parseDouble(data[3]);

                    if (id.isEmpty() || name.isEmpty() ||
                        quantity < 0 || price < 0) {
                        continue;
                    }

                    Product p = new Product(
                        id, name, quantity, price
                    );

                    inventory.addProduct(p);

                } catch (NumberFormatException e) {
                    System.out.println("Invalid record skipped.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading file!");
        }
    }
}

// Main Class
public class ProductInventorySearch {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Inventory inventory = new Inventory();

        // Load saved products
        FileHandler.loadFromFile(inventory);

        int choice = 0;

        do {

            System.out.println("\n===== PRODUCT INVENTORY SEARCH =====");
            System.out.println("1. Add Product");
            System.out.println("2. Display All Products");
            System.out.println("3. Search Product By ID");
            System.out.println("4. Search Product By Name");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:

                        System.out.print("Enter Product ID: ");
                        String id = sc.nextLine().trim();

                        System.out.print("Enter Product Name: ");
                        String name = sc.nextLine().trim();

                        if (id.isEmpty() || name.isEmpty()
                                || id.contains(",") || name.contains(",")) {
                            System.out.println("Invalid product details!");
                            break;
                        }

                        System.out.print("Enter Quantity: ");
                        int quantity = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter Price: ");
                        double price = Double.parseDouble(sc.nextLine());

                        if (quantity < 0 || price < 0) {
                            System.out.println("Invalid quantity or price!");
                            break;
                        }

                        Product p = new Product(
                            id, name, quantity, price
                        );

                        int oldSize = inventory.products.size();

                        inventory.addProduct(p);

                        if (inventory.products.size() > oldSize) {
                            FileHandler.saveToFile(inventory);
                        }

                        break;

                    case 2:

                        inventory.displayAll();
                        break;

                    case 3:

                        System.out.print("Enter Product ID: ");
                        String searchId = sc.nextLine().trim();

                        inventory.searchById(searchId);
                        break;

                    case 4:

                        System.out.print("Enter Product Name: ");
                        String searchName = sc.nextLine().trim();

                        inventory.searchByName(searchName);
                        break;

                    case 5:

                        FileHandler.saveToFile(inventory);

                        System.out.println("Data saved successfully!");
                        System.out.println("Thank you for using the application!");
                        break;

                    default:

                        System.out.println("Invalid choice! Try again.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter valid numeric input!");
            }

        } while (choice != 5);

        sc.close();
    }
}
