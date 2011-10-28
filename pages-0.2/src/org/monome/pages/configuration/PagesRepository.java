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
import org.monome.pages.pages.ArcPage;
import org.monome.pages.pages.Page;

public class PagesRepository {
	
	static Collection<Class<? extends Page>> pageTypes;
	static Collection<Class<? extends ArcPage>> arcPageTypes;
	
	static{
		try { 
			loadPages();
		} catch(Exception e) {
			System.out.println("PagesRepository: couldn't load page implementations.");
			e.printStackTrace();
		}
		
	}
	
	public static String[] getPageNames(){
		String[] res = new String[pageTypes.size()];
		int i = 0;
		for (Class<? extends Page> clz : pageTypes){
			res[i++] = clz.getName();
		}
		return res;
	}
	
    public static String[] getArcPageNames(){
        String[] res = new String[arcPageTypes.size()];
        int i = 0;
        for (Class<? extends ArcPage> clz : arcPageTypes){
            res[i++] = clz.getName();
        }
        return res;
    }

	
	static Page getPageInstance(String name, MonomeConfiguration conf, int index){
		Page page;
		for (Class<? extends Page> clz : pageTypes){
			if (clz.getName().equals(name)) {
				try {
					Constructor<? extends Page> ctor = clz.getConstructor(MonomeConfiguration.class, int.class);
					page = ctor.newInstance(conf, index);
					return page;
				} catch(Exception e) {
					System.out.println("PagesRepository: Failed to create page with name " + name + " on index " + index);
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
    static ArcPage getArcPageInstance(String name, ArcConfiguration arcConfiguration, int index){
        ArcPage page;
        for (Class<? extends ArcPage> clz : arcPageTypes){
            if (clz.getName().equals(name)) {
                try {
                    Constructor<? extends ArcPage> ctor = clz.getConstructor(ArcConfiguration.class, int.class);
                    page = ctor.newInstance(arcConfiguration, index);
                    return page;
                } catch(Exception e) {
                    System.out.println("PagesRepository: Failed to create arc page with name " + name + " on index " + index);
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

	
	public static void loadPages() throws IOException {
		  ClassLoader ldr = Thread.currentThread().getContextClassLoader();
		  Collection<Class<? extends Page>> pages = new ArrayList<Class<? extends Page>>();
		  Collection<Class<? extends ArcPage>> arcPages = new ArrayList<Class<? extends ArcPage>>();
		  
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
		        if (clz.getName().contains("org.monome.pages.pages.arc.")) {
		            Class<? extends ArcPage> impl = clz.asSubclass(ArcPage.class);
		            arcPages.add(impl);
		        } else {
                    Class<? extends Page> impl = clz.asSubclass(Page.class);
                    pages.add(impl);
		        }
		        //Constructor<? extends Page> ctor = impl.getConstructor();
		        //Page svc = ctor.newInstance();
		      }
		    }
		    catch(Exception ex){
		    	ex.printStackTrace();
		    }
		    finally {
		      try{
		    	  is.close();
		      }catch(Exception ex){}
		      	//do nothing
		    }
		  }
		  pageTypes = pages;
		  arcPageTypes = arcPages;
		}

}
