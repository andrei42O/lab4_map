package com.company.service;


import com.company.domain.Friendship;
import com.company.domain.User;
import com.company.domain.validators.FriendException;
import com.company.domain.validators.FriendshipValidator;
import com.company.repository.Repository;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceFriendship {
    private final Repository<Long, User> userRepo;
    private final Repository<Long, Friendship> friendshipRepo;
    private long ID;

    public ServiceFriendship(Repository<Long, User> userRepo, Repository<Long, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
        this.ID = loadID();
    }

    /**
     * The function provides and iterable list with all the friends of a user based on the user's ID
     * @param strID - String
     * @return Iterable<Long>
     */
    public Iterable<User> getFriends(String strID){
        Set<User> ret = new HashSet<>();
        if(userRepo.findOne(Long.parseLong(strID)) != null) {
            Long id = Long.parseLong(strID);
            friendshipRepo.findAll().forEach( friendship -> {
                if(friendship.getID1().equals(id))
                    ret.add(userRepo.findOne(friendship.getID2()));
                if(friendship.getID2().equals(id))
                    ret.add(userRepo.findOne(friendship.getID1()));
            });
            if(ret.size() > 0)
                return ret;
            else
                throw new FriendException("User with ID: " + id + " does not have any friends!\n");
        }
        throw new FriendException("The person with ID = " + strID + " does not exist");
    }

    /**
     * The function loads an ID that is currently not in use
     * @return Long (an ID that is not taken)
     */
    private Long loadID(){
        AtomicLong k = new AtomicLong();
        friendshipRepo.findAll().forEach(x ->{ if(Long.parseLong(k.toString()) <= x.getId()) k.set(x.getId() + 1);});
        return Long.parseLong(k.toString());
    }

    /**
     * The function removes a friendship between two Users
     * @param strID1 - String
     * @param strID2 - String
     */
    public void deleteFriendship(String strID1, String strID2) {
        //friendshipRepo.findAll().forEach(System.out::println);
        if(userRepo.findOne(Long.parseLong(strID1)) != null && userRepo.findOne(Long.parseLong(strID2)) != null) {
            Friendship ret = null;
            for (Friendship friendship : friendshipRepo.findAll())
                if(friendship.getID1().equals(Long.parseLong(strID1)) && friendship.getID2().equals(Long.parseLong(strID2))){
                    ret = friendshipRepo.delete(friendship.getId());
                    break;
                }
            if(ret != null)
                return;
        }
        throw new FriendException("An error occured, " + strID1 + " cannot be friend with " + strID2 + "\n");
    }

    /**
     * The function connects two users and make them friends
     * @param strID1 - String
     * @param strID2 - String
     */
    public void addFriendship(String strID1, String strID2) {
        if(userRepo.findOne(Long.parseLong(strID1)) != null && userRepo.findOne(Long.parseLong(strID2)) != null) {
            Friendship friendship = new Friendship(Long.parseLong(strID1), Long.parseLong(strID2));
            while(friendshipRepo.findOne(this.ID) != null)
                this.ID++;
            friendship.setId(this.ID++);
            Friendship ret = friendshipRepo.save(friendship);
            if(ret == null)
                return;
            User u1 = userRepo.findOne(Long.parseLong(strID1));
            User u2 = userRepo.findOne(Long.parseLong(strID2));
            throw new FriendException("'" + u1.getFirstName() + " " + u1.getLastName() + "' este deja prieten cu " +
                    "'" + u2.getFirstName() + " " + u2.getLastName() + "' din data de '" + friendship.getDate() + "'");
        }
        throw new FriendException("An error occured, " + strID1 + " cannot be friend with " + strID2 + "\n");
    }

    public Iterable<Friendship> findAll(){
        return friendshipRepo.findAll();
    }

    public Friendship searchFriendship(String rawID) {
        Friendship ret = friendshipRepo.findOne(Long.parseLong(rawID));
        if(ret == null)
            throw new FriendException("There is not friendship with this ID!\n");
        return ret;
    }
}
