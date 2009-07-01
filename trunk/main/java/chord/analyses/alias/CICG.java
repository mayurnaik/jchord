/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.analyses.alias;

import java.util.Set;

import joeq.Class.jq_Method;
import joeq.Compiler.Quad.Quad;

import chord.bddbddb.Rel.RelView;
import chord.doms.DomM;
import chord.project.ProgramRel;
import chord.util.SetUtils;
import chord.util.graph.AbstractGraph;
import chord.util.ArraySet;

/**
 * Implementation of a context-insensitive call graph.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public class CICG extends AbstractGraph<jq_Method> implements ICICG {
	private DomM domM;
	private ProgramRel relIM;
	private ProgramRel relMM;
	private ProgramRel relReachableM;
	public CICG(DomM domM, ProgramRel relIM, ProgramRel relMM,
			ProgramRel relReachableM) {
		this.domM = domM;
		this.relIM = relIM;
		this.relMM = relMM;
		this.relReachableM = relReachableM;
	}
	public Set<Quad> getCallers(jq_Method meth) {
		if (!relIM.isOpen())
			relIM.load();
		RelView view = relIM.getView();
		view.selectAndDelete(1, meth);
		Iterable<Quad> res = view.getAry1ValTuples();
		Set<Quad> invks = SetUtils.newSet(view.size());
		for (Quad invk : res)
			invks.add(invk);
		return invks;
	}
	public Set<jq_Method> getTargets(Quad invk) {
		if (!relIM.isOpen())
			relIM.load();
		RelView view = relIM.getView();
		view.selectAndDelete(0, invk);
		Iterable<jq_Method> res = view.getAry1ValTuples();
		Set<jq_Method> meths = SetUtils.newSet(view.size());
		for (jq_Method meth : res)
			meths.add(meth);
		return meths;
	}
	public int numRoots() {
		return 1;
	}
	public int numNodes() {
		if (!relReachableM.isOpen())
			relReachableM.load();
		return relReachableM.size();
	}
	public int numPreds(jq_Method node) {
		throw new UnsupportedOperationException();
	}
	public int numSuccs(jq_Method node) {
		throw new UnsupportedOperationException();
	}
	public Set<jq_Method> getRoots() {
		Set<jq_Method> roots = new ArraySet<jq_Method>(1);
		roots.add(domM.get(0));
		return roots;
	}
	public Set<jq_Method> getNodes() {
		if (!relReachableM.isOpen())
			relReachableM.load();
		Iterable<jq_Method> res = relReachableM.getAry1ValTuples();
		return SetUtils.iterableToSet(
			res, relReachableM.size());
	}
	public Set<jq_Method> getPreds(jq_Method meth) {
		if (!relMM.isOpen())
			relMM.load();
		RelView view = relMM.getView();
		view.selectAndDelete(1, meth);
		Iterable<jq_Method> res = view.getAry1ValTuples();
		return SetUtils.iterableToSet(
			res, view.size());
	}
	public Set<jq_Method> getSuccs(jq_Method meth) {
		if (!relMM.isOpen())
			relMM.load();
		RelView view = relMM.getView();
		view.selectAndDelete(0, meth);
		Iterable<jq_Method> res = view.getAry1ValTuples();
		return SetUtils.iterableToSet(
			res, view.size());
	}
	public Set<Quad> getLabels(jq_Method srcMeth, jq_Method dstMeth) {
		throw new UnsupportedOperationException();
	}
	public boolean calls(Quad invk, jq_Method meth) {
		if (!relIM.isOpen())
			relIM.load();
		return relIM.contains(invk, meth);
	}
	public boolean hasRoot(jq_Method meth) {
		return domM.get(meth) == 0;
	}
	public boolean hasNode(jq_Method meth) {
		if (!relReachableM.isOpen())
			relReachableM.load();
		return relReachableM.contains(meth);
	}
	public boolean hasEdge(jq_Method meth1, jq_Method meth2) {
		if (!relMM.isOpen())
			relMM.load();
		return relMM.contains(meth1, meth2);
	}
	/**
	 * Frees relations used by this call graph if they are in memory.
	 * <p>
	 * This method must be called after clients are done exercising
	 * the interface of this call graph.
	 */
	public void free() {
		if (relIM.isOpen())
			relIM.close();
		if (relMM.isOpen())
			relMM.close();
		if (relReachableM.isOpen())
			relReachableM.close();
	}
}

