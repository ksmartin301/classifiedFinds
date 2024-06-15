import java.text.*; //this will format the entered date into the correct format for the porgram to calculate if the payment date is past due
import java.util.*;
//Khaleah Martin CIS 334
//This is a java application that manages website credentials and payment dates. Assumes user only has one profile per website
public class StrictlyConfidential {
    private static final Map<String, String[]> credentials = new HashMap<>(); //uses its own hash function
    private static final Queue<String> expiryQueue = new LinkedList<>();

    public static int pin;
    
    private static void authenticate(int tempPin){
        while (true) {
            if (tempPin == pin) {
                System.out.println("\n Authentication successful!");
                return;
            } else {
                System.out.println("\n Incorrect PIN. Try again!");
                break;
            }
        }
    }

    private static void checkExpiryPasswords(){
        Date todaysDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todaysDate); // set calendar date to referrence
        calendar.add(Calendar.DAY_OF_YEAR, 7); //7 days days before setTime

        Date reminder = calendar.getTime();

        for (Map.Entry<String, String[]> expiry : credentials.entrySet()){
            String[] credentialSet = expiry.getValue();
            String websiteName = expiry.getKey();
            String expiryDate = credentialSet[2];

            if(expiryDate != null){
                try{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date dueDate = dateFormat.parse(expiryDate);

                    if(dueDate.before(todaysDate)){
                        System.out.println("\n Payment for '" + websiteName + "' is past due. \n Please update bill date.");
                    } else if (dueDate.before(reminder)){
                        System.out.println("\n Payment for '" + websiteName + "'' is due in 7 days. \n Please update soon.");
                        expiryQueue.offer(websiteName); //queue reminders
                    }
                } catch (ParseException e){
                    System.out.println("Could not parse payment date. Try again...");
                }
            } else{
                System.out.println(" ");
            }

        }

        processExpiryQueue();
    }

    private static void processExpiryQueue() {
        if (!expiryQueue.isEmpty()) {
            System.out.println("\n Websites due for payment reminders in 7 days:");
            while (!expiryQueue.isEmpty()) {
                String website = expiryQueue.poll();
                System.out.println("- " + website);
            }
        }
    }
    
    //FUNCTIONS
    //ADD CREDENTIALS FUNCTION
    private static void addPassword(Scanner input) {
        System.out.println("\nEnter website name: ");
        String websiteName = input.nextLine();

        System.out.println("\nEnter your username: ");
        String username = input.nextLine();

        System.out.println("\nEnter your password: ");
        String password = input.nextLine();

        String date;

        System.out.println("\nDoes this account have a subscription or trial end date? (Y/N)");
        String expire = input.nextLine().toUpperCase();
            if (expire.equals("Y")){
                
                System.out.println("\n Enter your subscription/trial end date (YYYY-MM-DD): ");
                date = input.nextLine();

                //Parse to Date object
                try{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date expiryDate = dateFormat.parse(date);
                    date = dateFormat.format(expiryDate); // back to string

                    expiryQueue.offer(date); // date for queue
                } catch (ParseException e){
                    expire = null;
                    System.out.println("Invalid date format. Edit from menu.");
                }
            }else{
                expire = null;
                date = null;
            }

        String[] credentialSet = {username, password, date};
        credentials.put(websiteName, credentialSet);
        System.out.println("\n Account information added successfully!");
    }

    //REMOVE CREDENTIALS FUNCTION
    private static void removePassword(Scanner input) {
        System.out.println("\n Enter website name to remove password: ");
        String site = input.nextLine();

        if (credentials.containsKey(site)) {
            credentials.remove(site);
            System.out.println("\n Account information removed successfully!");
        } else {
            System.out.println("\n Website not found in the records.");
        }

    }

    // EDIT USERNAME/PASSWORD FUNCTIONS
    //user
    private static void editUsername(Scanner input){
        System.out.println("\n Enter website name to edit username: ");
        String site = input.nextLine();

        if (credentials.containsKey(site)) {
            System.out.println("\n Enter new username: ");
            String newUsername = input.nextLine();
            String[] existingCredentials = credentials.get(site);
            existingCredentials[0] = newUsername;
            credentials.put(site, existingCredentials);
            System.out.println("\n Username updated successfully!");
        } else {
            System.out.println("\n Website not found in the records.");
        }
    }

    //password
    private static void editPassword(Scanner input) {
        System.out.println("\n Enter website name to edit password: ");
        String site = input.nextLine();

        if (credentials.containsKey(site)) {
            System.out.println("\n Enter new password: ");
            String newPassword = input.nextLine();
            String[] existingCredentials = credentials.get(site);
            existingCredentials[1] = newPassword;
            credentials.put(site, existingCredentials);
            System.out.println("\n Password updated successfully!");
        } else {
            System.out.println("\n Website not found in the records.");
        }
    }

    //payment date
    private static void editPaymentDate (Scanner input){
        System.out.println("\nEnter website name to edit expiry date: ");
        String websiteName = input.nextLine();

    if (credentials.containsKey(websiteName)) {
        String[] credentialSet = credentials.get(websiteName);
        String existingDate = credentialSet[2]; // Expiry date is at index 2 in the array

        if (existingDate != null){
            System.out.println("\n Current payment date ste: " + existingDate);
            System.out.println("\n Enter the new expiry date (YYYY-MM-DD): ");
            String newDate = input.nextLine();
                try{
                    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
                    Date expiryDate = dateFormat.parse(newDate);
                    newDate = dateFormat.format(expiryDate); //back to string

                    //update hash
                    credentialSet[2] = newDate;
                    credentials.put(websiteName, credentialSet);

                    //update queue
                    if (expiryQueue.contains(existingDate)){
                        expiryQueue.remove(existingDate);
                        expiryQueue.offer(newDate);
                    }

                    System.out.println("\n Expiry date updated seccessfully");
                } catch (ParseException e){
                    System.out.println("Invalid date format. Expiry date not updated.");
                }
            } else {
                System.out.println("\n No expiry date found for this website.");
            }
        } else {
            System.out.println("\n Website not found in the records.");
        }
    }

    //VIEW CREDENTIALS FUNCTION
    private static void viewPasswords() {
        if (credentials.isEmpty()) {
            System.out.println("\n No passwords stored yet.");
        }

        //map to array for sorting
        String[] websites = credentials.keySet().toArray(new String[0]);
        mergeSort(websites, 0, websites.length-1); // this will sort the credentions in alphabetical order

            System.out.println("\n Website Credentials:");//print sorted array
            for (String website : websites) {
                String[] credential = credentials.get(website);
                String username = credential[0];
                String password = credential[1];
                String date = credential[2];

                if (date == null){
                    System.out.println("\n Website: " + website + "\n Username: " + username + "\n Password: " + password);
                }else{
                    System.out.println("\n Website: " + website + "\n Username: " + username + "\n Password: " + password + "\n Payment due: " + date);
                }
            }
            checkExpiryPasswords();
            
        }
// merge sort cited from: CIS334 Chapt 9 Sorting slides 14-18
        //This is a recursive function that splits the array in half until it cannot be broken up anymore
        public static void mergeSort(String[] websites, int left, int right) {
            if (left < right) { //base case
                int median;
                if(right%2 == 0){ //even number of elements
                    median = (left + right) / 2;
                }else{ //odd number of elements
                    median = ((left+right)-1) / 2;
                }
                mergeSort(websites, left, median); //left half
                mergeSort(websites, median + 1, right); //right half
                merge(websites, left, median, right); //rearranges and rebuilds
            }
        }

        //This method sorts and merges the websites array back together
        public static void merge(String[] websites, int left, int mid, int right) {
            int a = mid - left + 1; //end of left sub array
            int z = right - mid; //beginning of right sub array

            //mid(median) indicates the middle index
    
            //temp arrays for subarrays
            String[] leftSide = new String[a];
            String[] rightSide = new String[z];
    
            for (int i = 0; i < a; ++i) {
                leftSide[i] = websites[left + i]; //copy into temp array until end of left side
            }
            for (int j = 0; j < z; ++j) {
                rightSide[j] = websites[mid + 1 + j]; //copy into temp array from beginning of right side
            }
    
            int i = 0; 
            int j = 0; 
            int k = left;
            while (i < a && j < z) { //compares both sides and puts smaller element back into website array
                if (leftSide[i].compareTo(rightSide[j]) <= 0) {
                    websites[k] = leftSide[i];
                    i++;
                } else {
                    websites[k] = rightSide[j];
                    j++;
                }
                k++;
            }
            
            //handles any remaining elements
            while (i < a) {
                websites[k] = leftSide[i];
                i++;
                k++;
            }
    
            while (j < z) {
                websites[k] = rightSide[j];
                j++;
                k++;
            }
        }
    
    

    //SEARCH A CREDENTIAL FUNCTION
    private static void searchCredentials(Scanner input) {
        System.out.println("\n Enter website name to search for credentials: ");
        String site = input.nextLine();
    
        boolean found = false;
        for (Map.Entry<String, String[]> search : credentials.entrySet()) { //map entry allows me to access and retrieve a specific key value pair 
            //in the credentials map based on the users input, then I can use this to display the search
            String website = search.getKey();
            if (website.equalsIgnoreCase(site)) {
                found = true;
                String username = search.getValue()[0];
                String password = search.getValue()[1];
                String date = search.getValue()[2];

                if (date.isEmpty()){
                System.out.println("\n Website: " + website + "\n Username: " + username + "\n Password: " + password);
                } else{
                System.out.println("\n Website: " + website + "\n Username: " + username + "\n Password: " + password + "\n Payment due: " + date);

                }
            }
        }
        if (!found) {
            System.out.println("No credentials found for website: " + site);
        }
    }


    public static void main(String[] args) {
        //user sets up pin for authentication purposes, they will be asked for their pin before accessing any of the menu options
        Scanner input = new Scanner(System.in);
        System.out.println("Create your PIN: ");
        pin = input.nextInt();

        mainMenu(input);
    }

    public static void mainMenu (Scanner input){
        // authenticate user
        while (true) {
            System.out.println("\n Strictly Confidential");
            System.out.println("5 Star Password Manager");

            System.out.println("\n Enter your PIN to proceed: ");
            int tempPin = input.nextInt();

            authenticate(tempPin);
        
            //menu options
            System.out.println("\n Choose an operation:");
            System.out.println("1. Add an Account");
            System.out.println("2. Remove an Account");
            System.out.println("3. Edit a Username");
            System.out.println("4. Edit a Password");
            System.out.println("5. Edit Payment Date");
            System.out.println("6. View Passwords");
            System.out.println("7. Search for an Account");
            System.out.println("8. Exit");
            System.out.print("\n Enter your choice: ");

            int choice = input.nextInt();
            input.nextLine(); //consume remaining newline

            switch (choice) {
                case 1:
                    addPassword(input);
                    break;
                case 2:
                    removePassword(input);
                    break;
                case 3:
                    editUsername(input);
                    break;
                case 4:
                    editPassword(input);
                    break;
                case 5:
                    editPaymentDate(input);
                case 6:
                    viewPasswords();
                    break;
                case 7:
                    searchCredentials(input);
                    break;
                case 8:
                    System.out.println("\n Logging out...");
                    return;
                default:
                    System.out.println("\n Invalid choice. Please choose a number between 1-6.");
            }
        }

    }
}