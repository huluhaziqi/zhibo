import com.lin.MyTest.TestSvServer;
import com.lin.MyTest.model.biz.Activity;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZhiboSvServer.class)
public class Test {

    @Autowired
    private ActivityDao activityJdbcDao;


    @org.junit.Test
    public void test() {

        List<ActivityEntity> activityEntityList = activityJdbcDao.getByUserId(143356521);
        for (ActivityEntity activityEntity : activityEntityList) {
            System.out.println(activityEntity.getId());
        }

//        ActivityEntity activityEntity = new ActivityEntity();
//
//        activityEntity.setTitle("test");
//        activityEntity.setCreateTime(new Date(System.currentTimeMillis()));
//        activityEntity.setDetail("test");
//        activityEntity.setExpireTime(new Date(System.currentTimeMillis()));
//        activityEntity.setId(23);
//        activityEntity.setImageUrl("http");
//        activityEntity.setUserId(7);
//
//        activityJdbcDao.insert(activityEntity);
    }


}
