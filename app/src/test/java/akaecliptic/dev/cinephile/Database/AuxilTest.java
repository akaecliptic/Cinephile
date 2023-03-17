package akaecliptic.dev.cinephile.Database;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static akaecliptic.dev.cinephile.auxil.database.Functions.selectMovieWhereIn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class AuxilTest {
    private static final String expectedSelectThree = "SELECT * FROM 'movie_data' WHERE _id IN ( ?, ?, ? )";

    @Test
    public void test_selectMovieWhereIn() {
        var actual = selectMovieWhereIn(3);
        assertThat(actual, is(expectedSelectThree));

        var selectFive = selectMovieWhereIn(5);
        assertThat(selectFive, containsString("?, ?, ?, ?, ?"));
    }
}
