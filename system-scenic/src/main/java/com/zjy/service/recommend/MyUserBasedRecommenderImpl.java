package com.zjy.service.recommend;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MyUserBasedRecommenderImpl implements MyUserBasedRecommender1 {

	public List<RecommendedItem> userBasedRecommender(long userID,int size) {
		// step:1 构建模型 2 计算相似度 3 查找k紧邻 4 构造推荐引擎
		List<RecommendedItem> recommendations = null;
		try {

			Class.forName("com.mysql.jdbc.Driver");
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setServerName("localhost");//本地为localhost
			dataSource.setPort(3306);
			dataSource.setUser("root");
			dataSource.setPassword("root");
			dataSource.setDatabaseName("system-scenic");//数据库名
			DataModel dataModel=new MySQLJDBCDataModel(dataSource,"score_scenic","user_id","scenic_id","score","time");


			//UserSimilarity similarity1=new UncenteredCosineSimilarity(model);
			UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);//用PearsonCorrelation 算法计算用户相似度
			double v1 = similarity.userSimilarity(1L, 1L);
			double v2 = similarity.userSimilarity(1L, 4L);
			double v3 = similarity.userSimilarity(1L, 8L);
			double v4 = similarity.userSimilarity(1L, 5L);
            double v5 = similarity.userSimilarity(1L, 6L);
			double v6 = similarity.userSimilarity(1L, 7L);
			log.info(v1+"");log.info(v2+"");log.info(v3+"");log.info(v4+"");log.info(v5+"");log.info(v6+"");
			 UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);//计算用户的“邻居”，这里将与该用户最近距离为 3 的用户设置为该用户的“邻居”。
			Recommender recommender = new CachingRecommender(new GenericUserBasedRecommender(dataModel, neighborhood, similarity));//采用 CachingRecommender 为 RecommendationItem 进行缓存
			recommendations = recommender.recommend(userID, size);//得到推荐的结果，size是推荐结果的数目

			System.out.println("recommendations="+JSON.toJSONString(recommendations));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return recommendations;
	}






}
