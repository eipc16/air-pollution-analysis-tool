package org.ppietrzak.dataprocessor.domain.calculationrequests.repository.custom;

import org.ppietrzak.dataprocessor.domain.calculationrequests.model.CalculationRequest;

import java.util.List;

public interface CalculationRequestCustomRepository {
    List<CalculationRequest> findByOrderId(String orderId);
}
