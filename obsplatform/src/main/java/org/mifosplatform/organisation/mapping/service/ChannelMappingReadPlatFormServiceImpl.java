package org.mifosplatform.organisation.mapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.mapping.data.ChannelMappingData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ChannelMappingReadPlatFormServiceImpl implements ChannelMappingReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<ChannelMappingData> paginationHelper = new PaginationHelper<ChannelMappingData>();
	
	@Autowired
	public ChannelMappingReadPlatFormServiceImpl(final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate =  new JdbcTemplate(dataSource);
	}

	//this is to retrive a particular record 
	@Override
	public ChannelMappingData retrieveChannelMapping(Long channelmappingId) {
		
		try{
      	ChannelMappingMapper channelMappingMapper = new ChannelMappingMapper();
		String sql = "SELECT "+channelMappingMapper.schema()+" AND m.id = ?";
		return jdbcTemplate.queryForObject(sql, channelMappingMapper,new Object[]{channelmappingId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	
	
	}

	@Override
	public Page<ChannelMappingData> retrieveChannelMapping(SearchSqlQuery searchChannelMapping) {

		ChannelMappingMapper channelMappingMapper = new ChannelMappingMapper();
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(channelMappingMapper.schema());
        
        String sqlSearch = searchChannelMapping.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " product_id like '%"+sqlSearch+"%' OR"
	    			+ " channel_id like '%"+sqlSearch+"%' OR"
	    			+ " product_code like '%"+sqlSearch+"%' OR"
	    			+ " channel_name like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }


        if (searchChannelMapping.isLimited()) {
            sqlBuilder.append(" limit ").append(searchChannelMapping.getLimit());
        }

        if (searchChannelMapping.isOffset()) {
            sqlBuilder.append(" offset ").append(searchChannelMapping.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, channelMappingMapper);
	
	}
	

	private class ChannelMappingMapper implements RowMapper<ChannelMappingData> {
	    @Override
		public ChannelMappingData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final Long productId = rs.getLong("productId");
			final Long channelId = rs.getLong("channelId");
			final String productCode = rs.getString("productCode");
			final String channelName = rs.getString("channelName");
			
			return new ChannelMappingData(id, productId, channelId,productCode,channelName);
		}
	    
		public String schema() {
			
			return " m.id AS id,m.product_id as productId, s.product_code AS productCode, " +
				   " m.channel_id as channelId, c.channel_name AS channelName FROM b_prd_ch_mapping m " +
				   " join b_channel c on c.id = m.channel_id join b_product s on s.id = m.product_id "+
				   " where m.is_deleted = 'N' "; 
			
			
		}
	}


	
	
}
