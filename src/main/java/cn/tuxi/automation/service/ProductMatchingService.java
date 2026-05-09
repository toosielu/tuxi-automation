package cn.tuxi.automation.service;

import cn.tuxi.automation.domain.MatchedProduct;
import cn.tuxi.automation.domain.ProjectInput;

import java.util.List;

public interface ProductMatchingService {
    List<MatchedProduct> match(ProjectInput input);
}
