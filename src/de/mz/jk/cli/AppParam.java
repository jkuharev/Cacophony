package de.mz.jk.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mz.jk.jsix.libs.XJava;

public class AppParam
{
	private String name = null;
	private List<String> values = new ArrayList<String>();
	
	public AppParam(String name)
	{
		this.name = name;
		addValue( this.name );
	}

	public AppParam addValue(Object value)
	{
		this.values.add( value.toString() );
		return this;
	}
	
	public AppParam addValues(Collection<Object> paramValues)
	{
		for ( Object v : paramValues )
		{
			this.values.add( v.toString() );
		}
		return this;
	}
	
	public List<String> getValues()
	{
		return values;
	}
	
	public String toString()
	{
		return ( values.size() > 1 ? XJava.joinList( values, " " ) : name );
	}
}