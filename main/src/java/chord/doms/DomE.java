/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.doms;

import joeq.Compiler.Quad.Inst;
import joeq.Compiler.Quad.Quad;
import joeq.Compiler.Quad.Operator;
import joeq.Compiler.Quad.Operator.Getfield;
import joeq.Compiler.Quad.Operator.Putfield;
import joeq.Compiler.Quad.Operand.RegisterOperand;
import chord.program.Program;
import chord.program.visitors.IHeapInstVisitor;
import chord.project.Chord;

/**
 * Domain of statements that access (read or write) an
 * instance field, a static field, or an array element.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Chord(
	name = "E",
	consumedNames = { "M" }
)
public class DomE extends QuadDom implements IHeapInstVisitor {
	public void visitHeapInst(Quad q) {
		Operator op = q.getOperator();
		if (op instanceof Getfield) {
			if (!(Getfield.getBase(q) instanceof RegisterOperand))
				return;
		}
		if (op instanceof Putfield) {
			if (!(Putfield.getBase(q) instanceof RegisterOperand))
				return;
		}
		getOrAdd(q);
	}
	public String toXMLAttrsString(Inst i) {
		Quad q = (Quad) i;
		Operator op = q.getOperator();
		return super.toXMLAttrsString(q) +
			" rdwr=\"" + (Program.isWrHeapInst(op) ? "Wr" : "Rd") + "\"";
	}
}
