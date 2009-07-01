/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.doms;

import joeq.Class.jq_Class;
import joeq.Class.jq_Method;
import joeq.Compiler.Quad.Quad;
import joeq.Compiler.Quad.Operator.Invoke;
import chord.project.Chord;
import chord.project.ProgramDom;
import chord.visitors.IInvokeInstVisitor;

/**
 * Domain of argument and return variable positions of methods
 * and method invocation statements.
 * <p>
 * Let N be the largest number of arguments or return variables
 * of any method or method invocation statement.  Then, this
 * domain contains elements 0, 1, ..., N-1 in order.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Chord(
	name = "Z"
)
public class DomZ extends ProgramDom<Integer>
		implements IInvokeInstVisitor {
	private int maxArgs;
	public void init() {
		maxArgs = 0;
	}
	public void visit(jq_Class c) { }
	public void visit(jq_Method m) {
		int numFormals = m.getParamTypes().length;
		if (numFormals > maxArgs)
			grow(numFormals);
	}
	public void visitInvokeInst(Quad q) {
		int numActuals = Invoke.getParamList(q).length();
		if (numActuals > maxArgs)
			grow(numActuals);
	}
	public void grow(int newSize) {
		int oldSize = maxArgs;
		for (int i = oldSize; i < newSize; i++)
			set(new Integer(i));
		maxArgs = newSize;
	}
}
