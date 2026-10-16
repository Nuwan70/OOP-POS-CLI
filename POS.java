import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class Product {
private String id;
private String name;
private double price;
private int stock;
private int taxRate;

public Product(String id, String name, double price, int stock, int taxRate) {
this.id = id;
this.name = name;
this.price = price;
this.stock = stock;
this.taxRate = taxRate;
}

public String getId() { return  id;}
public String getName() { return name; }
public double getPrice() { return price;}
public int getStock() { return stock; }
public int getTaxRate() { return taxRate; }

public void reduceStock(int qty) {
this.stock -= qty;
}
}

class CartItem {
private Product product;
private int quantity;

public CartItem(Product product, int quantity) {
this.product = product;
this.quantity = quantity;
}

public Product getProduct() { return product; }
public int getQuantity() { return quantity; }

public void addQuantity(int qty) {
this.quantity += qty;
}

public double getTotal() {
return product.getPrice() * quantity;
}

public double getTax() {
return getTotal() * product.getTaxRate() / 100.0;
}
}

class Cart {
private ArrayList<CartItem> items = new ArrayList<>();

public ArrayList<CartItem> getItems() { return items; }

public void addItem(Product product, int qty) {
for (CartItem item : items) {
if (item.getProduct().getId().equals(product.getId())) {
item.addQuantity(qty);
return;
}
}
items.add(new CartItem(product, qty));
}

public boolean removeItem(String productId) {
for (int i = 0; i < items.size(); i++) {
if (items.get(i).getProduct().getId().equals(productId)) {
items.remove(i);
return true;
}
}
return false;
}

public CartItem findItem(String productId) {
for (CartItem item : items) {
if (item.getProduct().getId().equals(productId)) {
return item;
}
}
return null;
}

public boolean isEmpty() {
return items.isEmpty();
}

public void clear() {
items.clear();
}

public double getSubtotal() {
double total = 0;
for (CartItem item : items) {
total += item.getTotal();
}
return total;
}

public double getTax() {
double tax = 0;
for (CartItem item : items) {
tax += item.getTax();
}
return tax;
}

public double getTotal() {
return getSubtotal() + getTax();
}
}

public class POS {
static Scanner sc = new Scanner(System.in);
static ArrayList<Product> products = new ArrayList<>();
static Cart cart = new Cart();
static int receiptNo = 1;

public static void main(String[] args) {
products.add(new Product("G1", "Milk 1L", 2.50, 20, 0));
products.add(new Product("G2", "Bread", 1.80, 15, 0));
products.add(new Product("G3", "Eggs (12)", 3.20, 10, 0));
products.add(new Product("E1", "USB Cable", 6.00, 8, 10));
products.add(new Product("E2", "Earphones", 15.00, 5, 10));

int choice;
do {
printMenu();
System.out.print("Choose an option: ");
choice = readInt();

switch (choice) {
case 1: viewProducts(); break;
case 2: addToCart(); break;
case 3: viewCart(); break;
case 4: removeFromCart(); break;
case 5: checkout(); break;
case 0: System.out.println("Goodbye!"); break;
default: System.out.println("Invalid option!");
}
} while (choice != 0);

}

static void printMenu() {
System.out.println("\n========== JAVA MINI MART POS ==========");
System.out.println("1. View products");
System.out.println("2. Add item to cart");
System.out.println("3. View cart");
System.out.println("4. Remove item from cart");
System.out.println("5. Checkout");
System.out.println("0. Exit");
System.out.println("========================================");
}

static void viewProducts() {
System.out.println("-----------------------------------------");
System.out.printf("%-5s %-15s %8s %8s %5s%n", "ID", "Item", "Price", "Stock", "Tax");
System.out.println("-----------------------------------------");
for (Product p : products) {
System.out.printf("%-5s %-15s %8.2f %8d %4d%%%n",
p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getTaxRate());
}
System.out.println("-----------------------------------------");
}

static void addToCart() {
System.out.print("Enter product ID: ");
String id = sc.nextLine().trim().toUpperCase();

Product p = findProduct(id);
if (p == null) {
System.out.println("Product not found.");
return;
}

System.out.print("Enter quantity: ");
int qty = readInt();
if (qty <= 0) {
System.out.println("Invalid quantity!");
return;
}

int alreadyInCart = 0;
CartItem existing = cart.findItem(id);
if (existing != null) alreadyInCart = existing.getQuantity();

if (qty + alreadyInCart > p.getStock()) {
System.out.println("Not enough stock! Only " + p.getStock() + " left.");
return;
}

cart.addItem(p, qty);
System.out.println("Added " + qty + " x " + p.getName() + " to cart.");
}

static void viewCart() {
if (cart.isEmpty()) {
System.out.println("Cart is empty.");
return;
}

System.out.println("-----------------------------------------");
System.out.printf("%-5s %-15s %5s %8s %8s%n", "ID", "Item", "Qty", "Price", "Total");
System.out.println("-----------------------------------------");
for (CartItem item : cart.getItems()) {
Product p = item.getProduct();
System.out.printf("%-5s %-15s %5d %8.2f %8.2f%n",
p.getId(), p.getName(), item.getQuantity(), p.getPrice(), item.getTotal());
}
System.out.println("-----------------------------------------");
System.out.printf("Subtotal: %.2f%n", cart.getSubtotal());
System.out.printf("Tax: %.2f%n", cart.getTax());
System.out.printf("TOTAL: %.2f%n", cart.getTotal());
}

static void removeFromCart() {
System.out.print("Enter product ID to remove: ");
String id = sc.nextLine().trim().toUpperCase();

if (cart.removeItem(id)) {
System.out.println("Item removed.");
} else {
System.out.println("That item is not in the cart.");
}
}

static void checkout() {
if (cart.isEmpty()) {
System.out.println("Cart is empty.");
return;
}

viewCart();

System.out.println("Payment method: 1. Cash 2. Card");
System.out.print("Choose payment: ");
int pay = readInt();

if (pay == 1) {
System.out.print("Cash received: ");
double cash = readDouble();
double total = cart.getTotal();

if (cash < total) {
System.out.println("Not enough cash!");
return;
}

double change = cash - total;
reduceStock();
printReceipt("CASH", change);
cart.clear();

} else if (pay == 2) {
System.out.print("Last 4 digits of card: ");
String digits = sc.nextLine().trim();
reduceStock();
printReceipt("CARD **" + digits, 0.0);
cart.clear();

} else {
System.out.println("Invalid payment method!");
}
}

static void printReceipt(String paymentInfo, double change) {
DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
String now = LocalDateTime.now().format(dtf);

System.out.println("\n==============================");
System.out.println(" JAVA MINI MART");
System.out.println("Receipt #" + String.format("%04d", receiptNo++) + " " + now);
System.out.println("------------------------------");
System.out.printf("%-5s %-15s %5s %8s %8s%n", "ID", "Item", "Qty", "Price", "Total");
System.out.println("------------------------------");

for (CartItem item : cart.getItems()) {
Product p = item.getProduct();
System.out.printf("%-5s %-15s %5d %8.2f %8.2f%n",
p.getId(), p.getName(), item.getQuantity(), p.getPrice(), item.getTotal());
}

System.out.println("------------------------------");
System.out.printf("Subtotal: %.2f%n", cart.getSubtotal());
System.out.printf("Tax: %.2f%n", cart.getTax());
System.out.printf("TOTAL: %.2f%n", cart.getTotal());
System.out.println("------------------------------");
System.out.println("Paid by: " + paymentInfo);
System.out.printf("Change: %.2f%n", change);
System.out.println("==============================");
System.out.println(" Thank you! Come again.");
System.out.println("==============================");
}

static Product findProduct(String id) {
for (Product p : products) {
if (p.getId().equals(id)) return p;
}
return null;
}

static void reduceStock() {
for (CartItem item : cart.getItems()) {
item.getProduct().reduceStock(item.getQuantity());
}
}

static int readInt() {
try {
return Integer.parseInt(sc.nextLine().trim());
} catch (Exception e) {
return -1;
}
}

static double readDouble() {
try {
return Double.parseDouble(sc.nextLine().trim());
} catch (Exception e) {
return 0;
}
}
}