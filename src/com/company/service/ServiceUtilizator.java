package com.company.service;

import com.company.domain.Friendship;
import com.company.domain.User;
import com.company.domain.validators.FriendException;
import com.company.repository.Repository;
import com.company.service.algorithms.GraphBFS;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Collections.max;

public class ServiceUtilizator {
    private final Repository<Long, User> userRepo;
    private final Repository<Long, Friendship> friendshipRepo;
    private long ID;

    public ServiceUtilizator(Repository<Long, User> userRepo, Repository<Long, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        this.ID = loadID();
    }

    /**
     * The function creates and adds a user
     * @param firstName - String
     * @param lastName - String
     */
    public void add(String firstName, String lastName){
        User u = new User(firstName, lastName);
        u.setId(ID++);
        userRepo.save(u);
    }

    /**
     * The function deletes a user based on its ID
     * @param rawID - String
     */
    public void delete(String rawID) throws Exception {
        if(userRepo.delete(Long.valueOf(rawID)) != null) {
            Long id = Long.parseLong(rawID);
            List<Friendship> temp = new ArrayList<>();
            friendshipRepo.findAll().forEach(temp::add);
            for (Friendship friendship : temp) {
                if (friendship.getID1().equals(id) || friendship.getID2().equals(id))
                    friendshipRepo.delete(friendship.getId());
            }
            return;
        }
        throw new Exception("There is no user with this id in our social network!\n");
    }

    /**
     * The function loads an ID that is currently not in use
     * @return Long (an ID that is not taken)
     */
    private Long loadID(){
        AtomicLong k = new AtomicLong();
        userRepo.findAll().forEach(x ->{ if(Long.parseLong(k.toString()) <= x.getId()) k.set(x.getId() + 1);});
        return Long.parseLong(k.toString());
    }

    /**
     * The function provides an iterable collection with all the users
     * @return Iterable<User>
     */
    public Iterable<User> getUsers(){
        return userRepo.findAll();
    }


    /**
     * The function provides the number of communities we currently have in our social network
     * @return Integer (no of communities)
     */
    public int noOfCommunities(){
        GraphBFS<Long> graph = new GraphBFS<>();
        userRepo.findAll().forEach(x -> {
            graph.addEdge(x.getId(), x.getId());
            try{
                friendshipRepo.findAll().forEach(friendship ->{
                   if(friendship.getID1().equals(x.getId()) || friendship.getID2().equals(x.getId()))
                       graph.addEdge(friendship.getID1(), friendship.getID2());
                });
            } catch (Exception ignored) {}
        });
        return graph.getConnectedComponents().size();
    }

    /**
     * The function provides an iterable collection with all the users in the most sociable community
     * @return Iterable<User>
     */
    public Iterable<User> mostSociableCommunity() {
        GraphBFS<Long> graph = new GraphBFS<>();
        userRepo.findAll().forEach(x -> {
            try{
                friendshipRepo.findAll().forEach(friendship ->{
                    if(friendship.getID1().equals(x.getId()) || friendship.getID2().equals(x.getId()))
                        graph.addEdge(friendship.getID1(), friendship.getID2());
                });
            } catch (Exception ignored) {}
        });
        int max = -1;
        Long keyMax = null;
        Map<Long, Set<Long>> communities = graph.getConnectedComponents();
        for(Long root: communities.keySet().stream().toList()) {
            communities.get(root).add(root);
            GraphBFS<Long> tempGraph = new GraphBFS<>();
            communities.get(root).forEach(vec -> {
                try{
                    friendshipRepo.findAll().forEach(friendship ->{
                        if(friendship.getID1().equals(vec) || friendship.getID2().equals(vec))
                            tempGraph.addEdge(friendship.getID1(), friendship.getID2());
                    });
                } catch (Exception ignored) {}
            });
            int distance = tempGraph.getDiameter();
            if(max < distance) {
                max = distance;
                keyMax = root;
            }
        }
        if(keyMax == null)
            throw new FriendException("Nobody is friend with nobody!\n");
        List<User> rez = new ArrayList<>();
        communities.get(keyMax).forEach(x -> rez.add(userRepo.findOne(x)));
        return rez;
    }

    /**
     * The function finds a user based on he's ID
     * @param rawID - String
     * @return User
     * @throws Exception if there is no user with that ID
     */
    public User findUser(String rawID) throws Exception {
        User entity = userRepo.findOne(Long.parseLong(rawID));
        if(entity != null)
            return entity;
        throw new Exception("There is no user with this ID in this social network!\n");
    }

    /**
     * The function update's a user's fields
     * @param rawID - String
     * @param firstName - String
     * @param lastName - String
     * @throws Exception - If there is no user with that ID
     */
    public void updateUser(String rawID, String firstName, String lastName) throws Exception {
        User temp = new User(firstName, lastName);
        temp.setId(Long.parseLong(rawID));
        if (userRepo.update(temp) != null)
            throw new Exception("The user already exists hmm ID is loading wrong...!\n");
    }
}
