package com.company.tests;

import com.company.domain.Friendship;
import com.company.domain.User;
import com.company.domain.validators.FriendException;
import com.company.domain.validators.FriendshipValidator;
import com.company.domain.validators.UserValidator;
import com.company.domain.validators.ValidationException;
import com.company.repository.Repository;
import com.company.repository.file.FriendshipFile;
import com.company.repository.file.UserFile;
import com.company.service.ServiceFriendship;
import com.company.service.ServiceUtilizator;
import com.company.service.algorithms.GraphBFS;

import java.util.*;

public class Tests {

    public static void run() throws Exception {
        testValidator();
        testDomain();
        testRepo();
        testRepoFriendship();
        testGraphBFS();
        testService();
    }

    private static void testValidator(){
        UserValidator vali = new UserValidator();
        try  {
            vali.validate(new User("", ""));
            assert false;
        }
        catch(ValidationException e){
            assert e.getMessage().equals("The id cannot be null!\nThe first name cannot be empty!\nThe last name cannot be empty!\nThe first name must start with capital letter and only have letters!\nThe last name must start with capital letter and only have letters!\n");
        }
        User u = new User("Andrei", "Versace");
        u.setId((long) 0);
        vali.validate(u);
        try  {
            u.setFirstName("Andrei-Aureliu-");
            vali.validate(u);
            assert false;
        }
        catch(ValidationException e){
            assert e.getMessage().equals("The first name must start with capital letter and only have letters!\n");
        }
    }

    private static void testDomain(){
        User u = new User("Raul", "Versaci");
        assert u.getFirstName().equals("Raul");
        assert u.getLastName().equals("Versaci");
        assert u.getId() == null;

        u.setFirstName("Andrei");
        u.setLastName("Versace");
        u.setId((long)0);

        assert u.getFirstName().equals("Andrei");
        assert u.getLastName().equals("Versace");
        assert u.getId() == 0;

        User u2 = new User("dani", "boi");
        u2.setId(1L);
        User u3 = new User("vasile", "vasi");
        u3.setId(2L);

        Friendship fs1 = new Friendship(u.getId(), u2.getId());
        Friendship fs2 = new Friendship(u2.getId(), u3.getId());
        fs1.setId(0L);
        fs2.setId(1L);
        assert fs1 != fs2;
        assert fs1.getID1().equals(u.getId());
        assert fs1.getID2().equals(u2.getId());
        assert fs2.getID1().equals(u2.getId());
        assert fs2.getID2().equals(u3.getId());
        //assert fs1.getDate() < fs2.getDate();
    }

    private static void testRepo() {
        Repository<Long, User> repo = new UserFile("src/com/company/tests/testfile.txt", new UserValidator());
        List<User> l = new ArrayList<>();
        repo.findAll().forEach(l::add);
        assert l.size() == 0;
        User u = new User("Andrei", "Versace");
        u.setId(0L);
        repo.save(u);
        repo.findAll().forEach(l::add);
        assert l.size() == 1;
        assert repo.findOne(0L) == u;
        assert repo.findOne(0L).getId() == 0;
        assert repo.findOne(1L) == null;
        u.setFirstName("Vasile");
        u.setLastName("Versaci");
        repo.update(u);
        assert repo.findOne(0L) == u;
        assert repo.findOne(0L).getId() == 0;
        assert repo.findOne(0L).getFirstName().equals("Vasile");
        assert repo.findOne(0L).getLastName().equals("Versaci");

        try {
            repo.save(new User("", ""));
            assert false;
        }
        catch (ValidationException e){
            assert e.getMessage().equals("The id cannot be null!\nThe first name cannot be empty!\nThe last name cannot be empty!\nThe first name must start with capital letter and only have letters!\nThe last name must start with capital letter and only have letters!\n");
        }

        // did not save the same client
        assert repo.save(u) == u;
        User u2 = new User("Dani", "Boi");
        u2.setId(1L);
        assert repo.save(u2) == null;

        assert repo.delete(2L) == null;
        assert repo.delete(0L) == u;
        assert repo.delete(1L) == u2;

        l.clear();
        repo.findAll().forEach(l::add);
        assert l.size() == 0;

    }

    private static void testRepoFriendship(){
        Repository<Long, Friendship> repo = new FriendshipFile("src/com/company/tests/friendshiptestfile.txt", new FriendshipValidator());
        List<Friendship> l = new ArrayList<>();
        repo.findAll().forEach(l::add);
        assert l.size() == 0;

        Friendship fr = new Friendship(0L, 1L);
        fr.setId(0L);
        repo.save(fr);
        assert repo.findOne(1L) == null;
        repo.findAll().forEach(l::add);
        assert l.get(0).equals(fr);
        fr.setID1(3L);
        repo.update(fr);
        l.clear();
        assert repo.findOne(1L) == null;
        repo.findAll().forEach(l::add);
        assert l.get(0).equals(fr);
        l.clear();


        Friendship temp = repo.findOne(0L);
        assert temp == fr;
        assert repo.findOne(1L) == null;


        assert repo.delete(0L).equals(fr);
        assert repo.delete(0L) == null;

        repo.findAll().forEach(l::add);
        assert l.size() == 0;

    }

    private static void testGraphBFS(){
        GraphBFS<Integer> graph = new GraphBFS<>();

        assert graph.getDiameter() == 1;

        graph.addEdge(1, 2);
        assert graph.getDiameter() == 1;

        graph.addEdge(2, 3);
        assert graph.getDiameter() == 2;

        graph.addEdge(1, 3);
        assert graph.getDiameter() == 1;

        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        assert graph.getDiameter() == 3;
        assert graph.getVertices().equals(Arrays.asList(1, 2, 3, 4, 5));
        assert graph.getConnectedComponents().keySet().size() == 1;


        graph.addEdge(6, 7);
        assert graph.getVertices().equals(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        assert graph.getConnectedComponents().keySet().size() == 2;



    }

    private static void testService() throws Exception {
        Repository<Long, User> repo = new UserFile("src/com/company/tests/testfile.txt", new UserValidator());
        Repository<Long, Friendship> friendshipRepo = new FriendshipFile("src/com/company/tests/friendshiptestfile.txt", new FriendshipValidator());
        ServiceUtilizator userServ = new ServiceUtilizator(repo, friendshipRepo);
        ServiceFriendship friendshipServ = new ServiceFriendship(repo, friendshipRepo);

        List<User> users = new ArrayList<>();
        userServ.getUsers().forEach(users::add);
        assert users.size() == 0;

        userServ.add("Andrei", "Versace");
        userServ.add("Andrei", "Back");
        userServ.add("Dani", "Boi");

        assert userServ.noOfCommunities() == 3;

        userServ.getUsers().forEach(users::add);
        assert users.size() == 3;
        assert users.get(0).equals(new User("Andrei", "Versace")) && users.get(0).getId() == 0;
        assert users.get(1).equals(new User("Andrei", "Back")) && users.get(1).getId() == 1;
        assert users.get(2).equals(new User("Dani", "Boi")) && users.get(2).getId() == 2;

        friendshipServ.addFriendship("0", "1");
        assert userServ.noOfCommunities() == 2;

        users.clear();
        userServ.mostSociableCommunity().forEach(users::add);
        assert users.equals(Arrays.asList(new User("Andrei", "Versace"), new User("Andrei", "Back")));


        friendshipServ.addFriendship("0", "2");
        assert userServ.noOfCommunities() == 1;


        users.clear();
        friendshipServ.getFriends("0").forEach(users::add);
        assert users.size() == 2;
        assert users.get(0).equals(new User("Dani", "Boi"));
        assert users.get(1).equals(new User("Andrei", "Back"));


        users.clear();
        friendshipServ.getFriends("2").forEach(users::add);
        assert users.size() == 1;
        assert users.get(0).equals(new User("Andrei", "Versace"));

        userServ.delete("2");
        users.clear();
        friendshipServ.getFriends("0").forEach(users::add);
        assert users.size() == 1;
        assert users.get(0).equals(new User("Andrei", "Back"));

        users.clear();
        userServ.getUsers().forEach(users::add);
        assert users.size() == 2;
        assert users.get(0).equals(new User("Andrei", "Versace")) && users.get(0).getId() == 0;
        assert users.get(1).equals(new User("Andrei", "Back")) && users.get(1).getId() == 1;

        try { friendshipServ.getFriends("2"); assert false; }
        catch (FriendException e) {
            assert e.getMessage().equals("The person with ID = 2 does not exist");
        }

        friendshipServ.deleteFriendship("0", "1");

        try { friendshipServ.getFriends("0"); assert false; }
        catch (FriendException e) { assert e.getMessage().equals("User with ID: 0 does not have any friends!\n"); }

        friendshipServ.addFriendship("0", "1");
        userServ.delete("0");
        assert userServ.noOfCommunities() == 1;
        userServ.delete("1");
        assert userServ.noOfCommunities() == 0;
    }

}
