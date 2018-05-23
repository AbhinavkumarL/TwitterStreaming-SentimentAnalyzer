import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import sys.process._
import org.apache.spark._
import org.apache.spark.graphx._
// To make some of the examples work we will also need RDD
import org.apache.spark.rdd.RDD
  object Part2 {
    def main(args: Array[String]): Unit = {

      if (args.length == 0) {
        println("I need two parameters! Input file and Output File ")
      }

      val sc = new SparkContext(new SparkConf().setAppName("Part2").setMaster("local"))
      val spark = SparkSession
        .builder()
        .appName("Part2")
        .master("local")
        .getOrCreate()
      import spark.implicits._

//      val sc = new SparkContext(new SparkConf().setAppName("Part2"))
//      val spark = SparkSession.builder.appName("Part2").getOrCreate()
      val sqlcon = spark.sqlContext
//      import spark.implicits._
      //var df=sqlcon.read.format("com.databricks.spark.csv").option("header","true").csv(args(0))
      val input=sc.textFile(args(0))
      var output=""
      //parser function
      case class Wiki(from:Long, to:Long)
      def MyDataParser(str: String): Wiki = {
        val line = str.split("\\W+")
        Wiki(line(0).toLong, line(1).toLong)
      }
      val wiki_rdd = input.filter(_.charAt(0)!='#').map(MyDataParser).cache()
      val v = wiki_rdd.map(x => (x.from.toLong, x.from)).distinct //all the vertices

      val e: RDD[Edge[String]] =  wiki_rdd.map(x => Edge(x.from.toLong, x.to.toLong, "temp")) //all the edges

      val graph = Graph(v, e)
      val out_degree=graph.outDegrees.reduceByKey((x,y) => x+y).sortBy(-_._2).take(5)//A-1 top 5 nodes with highest outdegree

      output+="1.The top 5 nodes with highest outdegree are as follow\n"
      out_degree.foreach(x=>output+={"node is "+x._1+" and have "+x._2+" edges\n"})

      val in_degree=graph.inDegrees.reduceByKey((x,y) => x+y).sortBy(-_._2).take(5)//A-2 top 5 nodes with highest indegree
      output+="\n2.The top 5 nodes with highest indegree are as follow\n"
      in_degree.foreach(x=>output+={"node is "+x._1+" and have "+x._2+" edges\n"})

      val page_rank = graph.pageRank(0.01).vertices//A-3 calculating the top 5 nodes with the highest pageranks values
      val top_rank=page_rank.sortBy(_._2,false).take(5)
      output+="\n3.The top 5 nodes with highest pagerank values are as follow\n"
      top_rank.foreach(x=>output+={"node is "+x._1+" and have page rank value "+x._2+"\n"})

      val c_c = graph.connectedComponents().vertices//A-4 finding the highest connected components
      val c_c_reverse = c_c.map{ case (x:VertexId,y:VertexId) => (y:VertexId , x:VertexId)}//reversing the x and y coordinates to count the right hand side
      val c_c_count = c_c_reverse.groupByKey.mapValues(_.toList.size)
      val final_output = c_c_count.sortBy(_._2,false).take(5)
      output+="\n4.The top 5 components with largest number of nodes are as follows:\n"
      final_output.foreach(x=>output+={"component is "+x._1+" with largest number of nodes as "+x._2+"\n"})

      val triangle_count = graph.triangleCount().vertices//A-5 applying the triangle count algorithm and finding the top 5 vertices with the largest triangle count
      val largest_count=triangle_count.sortBy(_._2, false).take(5)
      output+="\n5.The top 5 vertices with largest triangle count are:\n"
      largest_count.foreach(x=>output+={"node is "+x._1+" with largest triangle count is "+x._2+"\n"})

      var rdd :RDD[String]= null
      rdd=sc.parallelize(List(output))

      rdd.coalesce(1,true).saveAsTextFile(args(1))

    }
  }