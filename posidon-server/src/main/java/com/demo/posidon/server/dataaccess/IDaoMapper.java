package com.demo.posidon.server.dataaccess;

import com.demo.posidon.server.models.PosidonAlloc;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IDaoMapper {
    @Update({"UPDATE posidon_segment Set max_id = max_id + step WHERE service_name = #{serviceName}"})
    void updateMaxIdAndGetLeafAlloc(String serviceName);

    @Update({"UPDATE posidon_segment Set step = step WHERE service_name = #{serviceName}"})
    void updateMaxIdByCustomStepAndGetLeafAlloc(PosidonAlloc leafAlloc);

    @Select("SELECT max_id, step, service_name FROM posidon_segment WHERE service_name = #{serviceName}")
    @Results(
            value = {
                    @Result(property = "serviceName", column = "service_name"),
                    @Result(property = "maxValue", column = "max_id"),
                    @Result(property = "step", column = "step")
            }
    )
    PosidonAlloc getAllocBySeriveName(String serviceName);

    @Select({"SELECT service_name FROM posidon_segment"})
    List<String> getAllSupportedServices();
}
