package com.gome.stage.service.impl;

import com.gome.stage.entity.Label;
import com.gome.stage.entity.LabelIndex;
import com.gome.stage.entity.NewsRelatedLabel;
import com.gome.stage.entity.TitleIndex;
import com.gome.stage.mapper.LabelMapper;
import com.gome.stage.service.LabelService;
import com.gome.stage.utils.PaginatedArrayList;
import com.gome.stage.utils.PaginatedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LabelServiceImpl implements LabelService {

	@Autowired
	private LabelMapper labelDao;
	
	private final static String FIRSTPAGE_INDEXLIST_KEY = "cindexList"; // 聚合页索引KEY

	@Override
	public Label getLabelnameById(int id) {
		return labelDao.getLabelnameById(id);
	};
	
	@Override
	public List<Label> getNewsRelatedLabelByNewsID(int news) {
		return labelDao.getNewsRelatedLabelByNewsID(news);
	}
	
	@Override
	public List<NewsRelatedLabel> getNewsRelatedLabel(Map<String, Object> map) {
		return labelDao.getNewsRelatedLabel(map);
	}
	
	@Override
	public int updateLabelStates(int id){
		return labelDao.updateLabelStates(id);
	}

	
	public List<Label> getLabelRelEverySearch(){
		return labelDao.getLabelRelEverySearch();
	}
	
	@Override
	public List<LabelIndex> getLabelIndex(Map<String, Object> search) {
		return labelDao.getLabelIndex(search);
	}
	
	/**
	 * 标签聚合页
	 * @param site
	 * @param data
	 */
	@Override
	public void queryLabelIndexPageData(String site, Map<String, Object> data) {
		// 获得站点索引列表
		Map<String, Object> search = new HashMap<String, Object>();
		search.put("site", site); // 站点类型

		List<LabelIndex> manualIndexList = this.getLabelIndex(search);

		processLabelIndexList2Map(manualIndexList, data);

	}
	
	/**
	 * 获得标签索引数据
	 * 
	 * @param search
	 * @return
	 */
	@Override
	public Map<String, Object> getLabelMapWithPage(Map<String, Object> search) {
		PaginatedList<TitleIndex> paginatedArrayList = new PaginatedArrayList<TitleIndex>(
				(Integer) search.get("totalCount2"), (Integer) search.get("pageNumber"),
				(Integer) search.get("pageSize"));
		search.put("begin",paginatedArrayList.getStartPos());
		search.put("pagesize",paginatedArrayList.getPageSize());

		List<LabelIndex> labelIndexList = labelDao.getLabelIndexWithPage(search);
		Map<String, Object> data = new HashMap<String, Object>();

		if (null != labelIndexList) {
			data.put(FIRSTPAGE_INDEXLIST_KEY, labelIndexList);
		}
		return data;
	}
	
	/**
	 * 
	 * @param manualIndexList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void processLabelIndexList2Map(List<LabelIndex> manualIndexList, Map<String, Object> mapData) {

		if (null == mapData) {
			mapData = new HashMap<String, Object>();
		}

		if (null == manualIndexList || manualIndexList.isEmpty()) {
			return;
		}

		List<Map<String, Object>>[] indeListArr = new List[27];

		char[] charArr = new char[27];

		/*
		 * 初始化数据集
		 */
		for (int i = 0; i < indeListArr.length; i++) {
			indeListArr[i] = new ArrayList<Map<String, Object>>();
			if (i == (indeListArr.length - 1)) {
				charArr[i] = '0';
			} else {
				charArr[i] = (char) (65 + i);
			}
		}
		/*
		 * 筛选数据
		 */
		for (LabelIndex index : manualIndexList) {

			Map<String, Object> data = new HashMap<String, Object>();
			String cindex = index.getCindex();
			String tname = index.getTitle();
			Integer labelId = index.getLabelId();
			int titleId = index.getLabelId();
			char cx = '0';
			if (null != cindex && !"".equals(cindex.trim())) {
				cx = cindex.trim().charAt(0);
			}
			data.put("labelname", tname);
			data.put("labelId", labelId);

			switch (cx) {

			case 'A':
				indeListArr[0].add(data);
				break;
			case 'B':
				indeListArr[1].add(data);
				break;
			case 'C':
				indeListArr[2].add(data);
				break;
			case 'D':
				indeListArr[3].add(data);
				break;
			case 'E':
				indeListArr[4].add(data);
				break;
			case 'F':
				indeListArr[5].add(data);
				break;
			case 'G':
				indeListArr[6].add(data);
				break;
			case 'H':
				indeListArr[7].add(data);
				break;
			case 'I':
				indeListArr[8].add(data);
				break;
			case 'J':
				indeListArr[9].add(data);
				break;
			case 'K':
				indeListArr[10].add(data);
				break;
			case 'L':
				indeListArr[11].add(data);
				break;
			case 'M':
				indeListArr[12].add(data);
				break;
			case 'N':
				indeListArr[13].add(data);
				break;
			case 'O':
				indeListArr[14].add(data);
				break;
			case 'P':
				indeListArr[15].add(data);
				break;
			case 'Q':
				indeListArr[16].add(data);
				break;
			case 'R':
				indeListArr[17].add(data);
				break;
			case 'S':
				indeListArr[18].add(data);
				break;
			case 'T':
				indeListArr[19].add(data);
				break;
			case 'U':
				indeListArr[20].add(data);
				break;
			case 'V':
				indeListArr[21].add(data);
				break;
			case 'W':
				indeListArr[22].add(data);
				break;
			case 'X':
				indeListArr[23].add(data);
				break;
			case 'Y':
				indeListArr[24].add(data);
				break;
			case 'Z':
				indeListArr[25].add(data);
				break;
			case '0':
				indeListArr[26].add(data);
				break;
			case '1':
				indeListArr[26].add(data);
				break;
			case '2':
				indeListArr[26].add(data);
				break;
			case '3':
				indeListArr[26].add(data);
				break;
			case '4':
				indeListArr[26].add(data);
				break;
			case '5':
				indeListArr[26].add(data);
				break;
			case '6':
				indeListArr[26].add(data);
				break;
			case '7':
				indeListArr[26].add(data);
				break;
			case '8':
				indeListArr[26].add(data);
				break;
			case '9':
				indeListArr[26].add(data);
				break;
			}

		}
		/*
		 * 设置数据
		 */

		List<Map<String, Object>> indeList = new ArrayList<Map<String, Object>>();
		for (int j = 0; j < indeListArr.length; j++) {
			Map<String, Object> tmpMap = new HashMap<String, Object>();
			if (null != indeListArr[j] && !indeListArr[j].isEmpty()) {
				tmpMap.put("cindex", charArr[j]);
				tmpMap.put("list", indeListArr[j]);
				indeList.add(tmpMap);
			}

		}
		/*
		 * 添加索引数据
		 */
		mapData.put(FIRSTPAGE_INDEXLIST_KEY, indeList);
	}
	
	
	@Override
	public List<Map<String, Object>> getLabelCindexCount() {
		List<Map<String, Object>> cindexCountList = labelDao.getLabelCindexCount();
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		if (null != cindexCountList) {
			for (Map<String, Object> map : cindexCountList) {
				String cindex = (String) map.get("cindex");
				Long counts = (Long) map.get("counts");
				data.put(cindex, counts);
			}
		}
		retList.add(data);
		return retList;
	}

}