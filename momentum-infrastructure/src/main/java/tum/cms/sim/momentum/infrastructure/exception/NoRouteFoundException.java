package tum.cms.sim.momentum.infrastructure.exception;

@SuppressWarnings("serial")
public class NoRouteFoundException extends Exception {
	
	private static String routeNotFound = "The routing model could not found a new route";
	
	public NoRouteFoundException() {
		super(routeNotFound);
	}
}
