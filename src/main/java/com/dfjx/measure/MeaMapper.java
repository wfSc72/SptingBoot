package com.dfjx.measure;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by Sc on 2018/5/7.
 */
@Mapper
public interface MeaMapper {

    @Select("select column_name from user_tab_cols where table_name='BSC_MEASURE'")
    public List<Map<String,Object>> reCol();

    //返回基础指标数值
    @Select("select measure_id,measure_name from bsc_measure t where parent_measure_id = 'root'")
    public List<Map<String,Object>> reBscMea();

    //返回根据基础指标中的parent_measure_id为measure_id指标值
    @Select("select measure_name,formula_desc,measure_id,measure_desc from bsc_measure where parent_measure_id =#{meaId}")
    public List<Map<String,Object>> reMeaName(@Param("meaId") String meaId);
    //select measure_name,formula_desc,measure_id from bsc_measure where source_id is not null and parent_measure_id =#{meaId}

    //返回基础指标的 source_id
    @Select("select source_id from bsc_measure where source_id is not null and rownum=1 and parent_measure_id =#{meaId}")
    public Map reSouceId(@Param("meaId") String meaId);

    @Select("<script>" +
            "select t.source_id,t.measure_id,t.measure_name from BSC_MEASURE t where 1=1"+
            "<if test=\"name!='' and name!=null\">and name=#{name}</if>"+
            "</script>")
    public List<Map<String,Object>> reMea(String meaId,String meaName);

    //--2018.5.21 发生过修改，将length后的条件改为=号
    //返回指标数据
    @Select("<script>" +
            "select r.month_id, r.measure_id, m.measure_name, o.object_id, o.object_name, r.value\n" +
            "from BSC_PROJ_OBJ o, bsc_result r, bsc_measure m"+  "\nwhere\n" +
            " o.project_id = r.project_id" +
            " and o.object_id = r.object_id" +
            " and m.measure_id = r.measure_id"+
            "<if test=\"meaId!='' and meaId!=null\"> and r.measure_id = #{meaId}</if>"+
            "<if test=\"fromYear!='' and fromYear!=null\"> and #{fromYear} &lt; = r.month_id and  length(r.month_id) = #{length}</if>"+
            "<if test=\"toYear!='' and toYear!=null\"> and r.month_id &lt; = #{toYear} and  length(r.month_id) = #{length}</if>"+
            "</script>")
    List<Map<String,Object>> reMeaSource(@Param("meaId") String meaId,@Param("fromYear") String fromYear,@Param("toYear") String toYear,@Param("length") int length);

    //返回指标数据
    @Select("<script>" +
            "select r.month_id, r.measure_id, m.measure_name, o.object_id, o.object_name, r.value\n" +
            "from BSC_PROJ_OBJ o, bsc_result r, bsc_measure m"+  "\nwhere\n" +
            "   o.project_id = r.project_id" +
            "   and o.object_id = r.object_id" +
            "   and m.measure_id = r.measure_id"+
            "    and r.measure_id in   " +
            "   <foreach  collection = \"meaId\" item=\"meaId\" open='(' separator=',' close=')'>" +
            "   #{meaId}" +
            "   </foreach>" +
            "<if test=\"from!='' and from!=null\"> and #{from} &lt; = r.month_id and  length(r.month_id) &gt;= #{length}</if>"+
            "<if test=\"to!='' and to!=null\"> and r.month_id &lt; = #{to} and  length(r.month_id) &gt;= #{length}</if>"+
            "</script>")
    List<Map<String,Object>> reMeaSourceList(@Param("meaId") String[] meaId,@Param("from") String Year,@Param("to") String to,@Param("length") int length);
    //,@Param("from") String Year,@Param("to") String to

    //返回指标体系
    @Select("select c.measure_id as category_id, c.measure_name as category_name,m.measure_id, m.measure_name\n" +
            "from\n" +
            "(select measure_id, measure_name from bsc_measure where parent_measure_id = 'root') c, bsc_measure m\n" +
            "where\n" +
            "c.measure_id = m.parent_measure_id\n" +
            "order by category_id, measure_id")
    List<Map<String,Object>> reMeaSys();

    //获取所有指标体系的父类节点 和 节点指标名称
    @Select("select measure_id, measure_name from bsc_measure where parent_measure_id = 'root'")
    List<Map<String,Object>> reParent();

    @Select("select\n" +
            "distinct(length(r.month_id))\n" +
            "from bsc_result r\n" +
            "where r.measure_id = #{meaid}")
    List<Integer> reDim(@Param("meaid") String meaId);

}
