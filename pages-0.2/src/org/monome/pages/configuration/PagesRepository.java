package org.monome.pages.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.monome.pages.configuration.MonomeConfiguration;
import org.monome.pages.pages.Page;

public class PagesRepository {
	
	static Collection<Class<? extends Page>> pageTypes;
	
	static{
		try { 
			pageTypes = loadPages();
		} catch(Exception e) {
			System.out.println("couldn't load page implementations!!! " + e);
		}
		
	}
	
	public static String[] getPageNames(){
		String[] res = new String[pageTypes.size()];
		int i = 0;
		for (Class<? extends Page> clz : pageTypes){
			res[i++] = clz.getName();
			System.out.println(clz.getName());
		}
		return res;
	}
	
	static Page getPageInstance(String name, MonomeConfiguration conf, int index){
		Page page;
		for (Class<? extends Page> clz : pageTypes){
			System.out.println("compare '" + clz.getName() + "' to '" + name + "'");
			if (clz.getName().equals(name)) {
				try {
					Constructor<? extends Page> ctor = clz.getConstructor(MonomeConfiguration.class, int.class);
					page = ctor.newInstance(conf, index);
					return page;
				} catch(Exception e) {
					System.out.println("Page couldn't be created!!! " + e);
				}
			}
		}
		return null;
	}
	
	public static Collection<Class<? extends Page>> loadPages() throws IOException {
		  ClassLoader ldr = Thread.currentThread().getContextClassLoader();
		  Collection<Class<? extends Page>> pages = new ArrayList<Class<? extends Page>>();
		  
		  Enumeration<URL> e = ldr.getResources("META-INF/services/" + Page.class.getName());

		  while (e.hasMoreElements()) {
		    URL url = e.nextElement();
		    InputStream is = url.openStream();;
		    try {
		      
		      BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		      while (true) {
		        String line = r.readLine();
		        if (line == null)
		          break;
		        int comment = line.indexOf('#');
		        if (comment >= 0)
		          line = line.substring(0, comment);
		        String name = line.trim();
		        if (name.length() == 0)
		          continue;
		        Class<?> clz = Class.forName(name, true, ldr);
		        Class<? extends Page> impl = clz.asSubclass(Page.class);
		        //Constructor<? extends Page> ctor = impl.getConstructor();
		        //Page svc = ctor.newInstance();
		        pages.add(impl);
		      }
		    }
		    catch(Exception ex){
		    	System.out.println(ex);
		    }
		    finally {
		      try{
		    	  is.close();
		      }catch(Exception ex){}
		      	//do nothing
		    }
		  }
		  return pages;
		}

}
