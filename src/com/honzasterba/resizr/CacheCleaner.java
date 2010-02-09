package com.honzasterba.resizr;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.honzasterba.bigblobae.BigBlob;
import org.honzasterba.bigblobae.BigBlobFragment;

import com.honzasterba.resizr.cache.CacheRecord;
import com.honzasterba.resizr.cache.Persistence;

@SuppressWarnings("serial")
public class CacheCleaner extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");
		PersistenceManager pm = Persistence.get().getPersistenceManager();
		try {
			deleteAll(pm, CacheRecord.class.getName(), resp);
			deleteAll(pm, BigBlob.class.getName(), resp);
			deleteAll(pm, BigBlobFragment.class.getName(), resp);
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteAll(PersistenceManager pm, String clsName,
			HttpServletResponse resp) throws IOException {
		String queryStr = "select from " + clsName;
		Query query = pm.newQuery(queryStr);
		List list = (List) query.execute();
		resp.getWriter().println(
				clsName + ": About to delete " + list.size() + " records.");
		for (Object o : list) {
			pm.deletePersistent(o);
		}
		pm.flush();
		resp.getWriter().println(clsName + ": Done.");
	}

}
