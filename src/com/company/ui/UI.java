package com.company.ui;

import com.company.domain.Friendship;
import com.company.domain.User;
import com.company.domain.validators.FriendshipValidator;
import com.company.domain.validators.UserValidator;
import com.company.repository.DataBase.FriendshipDBRepository;
import com.company.repository.DataBase.UserDBRepository;
import com.company.repository.Repository;
import com.company.repository.file.FriendshipFile;
import com.company.repository.file.UserFile;
import com.company.service.ServiceFriendship;
import com.company.service.ServiceUtilizator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UI {
    private final BufferedReader buff;
    private final ServiceUtilizator userServ;
    private final ServiceFriendship friendshipServ;

    public UI(){
        this.buff = new BufferedReader(new InputStreamReader(System.in));
//        Repository<Long, User> repo = new UserFile("data/users.in", new UserValidator());
//        Repository<Long, Friendship> friendshipRepo = new FriendshipFile("data/friendships.in", new FriendshipValidator());
        Repository<Long, User> repo = new UserDBRepository( "jdbc:postgresql://localhost:5432/socialnetwork_map",
                                                            "postgres", "admin", new UserValidator());
        Repository<Long, Friendship> friendshipRepo = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork_map",
                                                                                "postgres", "admin", new FriendshipValidator());
        this.userServ = new ServiceUtilizator(repo, friendshipRepo);
        this.friendshipServ = new ServiceFriendship(repo, friendshipRepo);
    }

    private void showMenu(){
        System.out.println("1 - Add User");
        System.out.println("2 - Delete User");
        System.out.println("3 - Add Friend");
        System.out.println("4 - Remove Friendship");
        System.out.println("5 - Show a user's friends");
        System.out.println("6 - Show how many communities there are");
        System.out.println("7 - Show the most sociable community");
        System.out.println("8 - Show users");
        System.out.println("9 - Search a users by ID");
        System.out.println("10 - Modify a user");
        System.out.println("11 - Search a friendship");
        System.out.println("12 - Show all friendships");
        System.out.println("x - Exit");
        System.out.print(">>> ");
    }

    private void addUser() throws IOException {
        System.out.println("<-------- USER ADD -------->\n");
        System.out.println("First Name: ");
        String firstName = buff.readLine();
        System.out.println("Last Name: ");
        String lastName = buff.readLine();
        userServ.add(firstName, lastName);
    }

    private void showUsers(){
        userServ.getUsers().forEach(System.out::println);
    }

    private void deleteUser() throws Exception {
        System.out.println("<----- Write down the ID of the user you want to delete ----->\n");
        showUsers();
        String rawID = buff.readLine();
        userServ.delete(rawID);
    }

    private void searchUser() throws Exception {
        System.out.print("Introduceti ID-ul clientului pe care doriti sa il cautati:");
        String rawID = buff.readLine();
        System.out.println(userServ.findUser(rawID));
    }

    private void updateUser() throws Exception {
        showUsers();
        System.out.print("Introduceti id-ul utilizatorului pe care doriti sa il modificati: ");
        String rawID = buff.readLine();
        System.out.println("Introduceti noul prenume:");
        String firstName = buff.readLine();
        System.out.println("Introduceti noul nume:");
        String lastName = buff.readLine();
        userServ.updateUser(rawID, firstName, lastName);
    }

    private void addFriendship() throws IOException {
        System.out.println("<----- Write down the IDs of the users you want to connect as friends ----->\n");
        showUsers();
        System.out.println("First user's ID:\n>>> ");
        String rawID1 = buff.readLine();
        System.out.println("Second user's ID:\n>>> ");
        String rawID2 = buff.readLine();
        friendshipServ.addFriendship(rawID1, rawID2);
    }

    private void deleteFriendship() throws IOException {
        System.out.println("<----- Write down the IDs of the users you want to disconnect as friends ----->\n");
        showUsers();
        System.out.println("First user's ID:\n>>> ");
        String rawID1 = buff.readLine();
        System.out.println("Second user's ID:\n>>> ");
        String rawID2 = buff.readLine();
        friendshipServ.deleteFriendship(rawID1, rawID2);
    }

    private void showNoOfCommunities(){
        System.out.println("There are " + userServ.noOfCommunities() + " communities in our social network :)\n");
    }

    private void showMostActiveCommunity(){
        System.out.println("<-------- Most sociable community -------->\n");
        userServ.mostSociableCommunity().forEach(System.out::println);
    }

    private void showFriends() throws IOException {
        showUsers();
        System.out.println("Introduceti id-ul utilizatorului carui doriti sa ii vedeti prietenii: ");
        String rawID = buff.readLine();
        friendshipServ.getFriends(rawID).forEach(System.out::println);
    }

    public void run(){
        int command;
        boolean ok = true;
        while(ok){
            try {
                showMenu();
                String line = buff.readLine();
                if(line.equals("x"))
                    line = "1337";
                command = Integer.parseInt(line);
                switch (command) {
                    case 1 -> addUser();
                    case 2 -> deleteUser();
                    case 3 -> addFriendship();
                    case 4 -> deleteFriendship();
                    case 5 -> showFriends();
                    case 6 -> showNoOfCommunities();
                    case 7 -> showMostActiveCommunity();
                    case 8 -> showUsers();
                    case 9 -> searchUser();
                    case 10 -> updateUser();
                    case 11 -> searchFriendship();
                    case 12 -> showFriendships();
                    case 1337 -> { System.out.println("Shutting down..."); ok = false; }
                    default -> System.out.println("Invalid command!\n");
                }
            }
            catch (IOException e) {
                System.out.println("System.in doesn't work? lmao\n");
            }
            catch (NumberFormatException e){
                System.out.println("Invalid command!\n");
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    private void showFriendships() throws Exception{
        System.out.println("All friendships in the social network:");
        friendshipServ.findAll().forEach(friendship -> {
            try {
                User u1 = userServ.findUser(friendship.getID1().toString());
                User u2 = userServ.findUser(friendship.getID2().toString());
                System.out.println( "'" + u1.getFirstName() + " " + u1.getLastName() + "' s-a imprietenit cu " +
                                    "'" + u2.getFirstName() + " " + u2.getLastName() + "' in data de '" + friendship.getDate() + "'");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void searchFriendship() throws IOException {
        System.out.print("Introduceti ID-ul prieteniei pe care doriti sa o cautati: ");
        String rawID = buff.readLine();
        System.out.println(friendshipServ.searchFriendship(rawID));

    }



}
