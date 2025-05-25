package com.sismics.reader.core.dao.jpa.mapper;

import com.sismics.reader.core.dao.jpa.dto.BugReportDto;
import com.sismics.util.jpa.ResultMapper;

/**
 * Bug report result mapper.
 * 
 * @author yeshu
 */

public class BugReportMapper extends ResultMapper<BugReportDto> {
    @Override
    public BugReportDto map(Object[] o) {
        int i = 0;
        BugReportDto dto = new BugReportDto();
        dto.setId(stringValue(o[i++]));
        dto.setUsername(stringValue(o[i++]));
        dto.setDescription(stringValue(o[i++]));
        dto.setBugStatus(stringValue(o[i++]));
        dto.setDeleteStatus(stringValue(o[i++]));
        
        return dto;
    }
}