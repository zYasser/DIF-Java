package com.example.utils;

import com.example.models.ServiceDetails;

import java.util.Comparator;

public class ServiceDetailsConstructComparator implements Comparator<ServiceDetails>
{

	@Override
	public int compare(ServiceDetails serviceDetails1, ServiceDetails serviceDetails2) {

		if (serviceDetails1.getConstructor() == null || serviceDetails2.getConstructor() == null) {

			return 0;

		}


		return Integer.compare(

				serviceDetails1.getConstructor().getParameterCount(),

				serviceDetails2.getConstructor().getParameterCount()

		                      );

	}

}