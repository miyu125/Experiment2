package lang;


public interface Compiler<Pctx> {
	public abstract void semanticCheck(Pctx pcx) throws FatalErrorException;
	public abstract void codeGen(Pctx pcx) throws FatalErrorException;
}

