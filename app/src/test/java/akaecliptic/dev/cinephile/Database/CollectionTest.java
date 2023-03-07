package akaecliptic.dev.cinephile.Database;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.OrderWith;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Alphanumeric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import akaecliptic.dev.cinephile.data.accessor.SQLite;

@OrderWith(Alphanumeric.class)
@RunWith(RobolectricTestRunner.class)
public class CollectionTest {
    private SQLite sqlite = null;

    @Before
    public void init() {
        if(sqlite != null) return;

        sqlite = SQLite.getInstance(RuntimeEnvironment.getApplication());
    }

    /*          COLLECTION INSERTS           */

    @Test
    public void test_insertCollection() {

    }

    @Test
    public void test_selectCollection() {

    }

    @Test
    public void test_updateCollection() {

    }

}
