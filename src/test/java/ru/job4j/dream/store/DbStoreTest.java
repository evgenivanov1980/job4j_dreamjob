package ru.job4j.dream.store;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DbStoreTest {
    static Connection connection;

    @BeforeClass
    public static void initConnection() {
        try (InputStream in = DbStoreTest.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")

            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @After
    public void wipeTablePost() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from post")) {
            statement.execute();
        }
    }

    @After
    public void wipeTableCandidate() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from candidate")) {
            statement.execute();
        }
    }

    @Test
    public void whenSavePostAndFindByGeneratedIdThenMustBeTheSame() {
        Post post = new Post(0, "java junior");
        DbStore.instOf().savePost(post);
        assertThat(DbStore.instOf().findById(post.getId()), is(post));
    }

    @Test
    public void whenUpdatePostThenNewPost() {
        Post post = new Post(0, "java develop");
        int id = DbStore.instOf().savePost(post);
        Post newPpost = new Post(id, "update");
        DbStore.instOf().savePost(newPpost);
        assertThat(DbStore.instOf().findById(id).getName(), is("update"));
    }

    @Test
    public void whenSaveCandidateAndFindByGeneratedIdThenMustBeTheSame() {
        Candidate candidate = new Candidate(0, "Senior java");
        DbStore.instOf().saveCandidate(candidate);
        assertThat(DbStore.instOf().findCandidateById(candidate.getId()), is(candidate));
        System.out.println(((List<Candidate>) DbStore.instOf().findAllCandidates()).get(0).getId());
    }


    @Test
    public void whenUpdateCandidateThenNewCandidate() {
        Candidate candidate = new Candidate(0, "Middle Java");
        int id = DbStore.instOf().saveCandidate(candidate);
        Candidate newCandidate = new Candidate(id, "updated");
        DbStore.instOf().saveCandidate(newCandidate);
        assertThat(DbStore.instOf().findCandidateById(id).getName(), is("updated"));
    }
}