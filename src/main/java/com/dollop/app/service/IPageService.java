package com.dollop.app.service;

import java.util.List;

import org.springframework.data.domain.Page;

public interface IPageService {

	public <S, T> Page<S> getAllData(List<S> list, Page<T> p);
}
