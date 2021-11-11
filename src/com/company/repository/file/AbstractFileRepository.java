package com.company.repository.file;

import com.company.domain.Entity;
import com.company.domain.validators.Validator;
import com.company.repository.InMemoryRepository.InMemoryRepository;

import java.io.*;

import java.util.Arrays;
import java.util.List;


/**
 *This class extends the inmemory repository, so it can be decorated for the in file repository
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    private void loadData() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                List<String> attributes = Arrays.asList(line.split(";"));
                E entity = extractEntity(attributes);
                super.save(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes - List<String>
     * @return an entity of type E
     */
    protected abstract E extractEntity(List<String> attributes);

    /**
     * The function converts an entity to its string storage format
     * @param entity - E
     * @return - String
     */
    protected abstract String createEntityAsString(E entity);

    @Override
    public E save(E entity){
        if(super.save(entity) == null) {
            appendToFile(entity);
            return null;
        }
        return entity;
    }

    /**
     * This function saves an entity to file without rewriting all of them
     * @param entity - E
     */
    protected void appendToFile(E entity){
        try (BufferedWriter buff = new BufferedWriter(new FileWriter(fileName, true))){
            try {
                buff.write(createEntityAsString(entity));
                buff.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This functions stores all the entities' data ino file
     */
    protected void writeToFile(){
        try (BufferedWriter buff = new BufferedWriter(new FileWriter(fileName))) {
            super.findAll().forEach(entity ->{
                try {
                    buff.write(createEntityAsString(entity));
                    buff.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public E update(E entity) {
        if(super.update(entity) == null){
            writeToFile();
            return null;
        }
        return entity;
    }

    @Override
    public E delete(ID id) {
        E entity = super.delete(id);
        if(entity != null) {
            writeToFile();
            return entity;
        }
        return null;
    }

}

