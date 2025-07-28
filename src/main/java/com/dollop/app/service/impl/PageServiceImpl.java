package com.dollop.app.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.dollop.app.exception.InvalidResourceException;
import com.dollop.app.service.IPageService;

@Service
public class PageServiceImpl implements IPageService {

	@Override
	public <S, T> Page<S> getAllData(List<S> list, Page<T> p) {

		if (p.isEmpty()) {
			throw new InvalidResourceException("invalid page Number,you have only " + p.getTotalPages() + " pages");
		}
		System.err.println(p + "\n" + new PageImpl<S>(list, p.getPageable(), p.getTotalElements()));
		return new PageImpl<S>(list, p.getPageable(), p.getTotalElements());
	}

}
