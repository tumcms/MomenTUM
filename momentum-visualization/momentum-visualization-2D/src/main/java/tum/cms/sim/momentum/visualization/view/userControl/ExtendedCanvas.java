/*******************************************************************************
 * Welcome to the pedestrian simulation framework MomenTUM. 
 * This file belongs to the MomenTUM version 2.0.2.
 * 
 * This software was developed under the lead of Dr. Peter M. Kielar at the
 * Chair of Computational Modeling and Simulation at the Technical University Munich.
 * 
 * All rights reserved. Copyright (C) 2017.
 * 
 * Contact: peter.kielar@tum.de, https://www.cms.bgu.tum.de/en/
 * 
 * Permission is hereby granted, free of charge, to use and/or copy this software
 * for non-commercial research and education purposes if the authors of this
 * software and their research papers are properly cited.
 * For citation information visit:
 * https://www.cms.bgu.tum.de/en/31-forschung/projekte/456-momentum
 * 
 * However, further rights are not granted.
 * If you need another license or specific rights, contact us!
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package tum.cms.sim.momentum.visualization.view.userControl;

import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import tum.cms.sim.momentum.visualization.model.GestureModel;

/**
 * 
 * @author ga37sib
 *
 */
public class ExtendedCanvas extends Pane {

    private Pane rotatableChild = null;
    
	public Pane getRotatableChild() {
		return rotatableChild;
	}

	public void setRotatableChild(Pane rotatableChild) {
		
		this.rotatableChild = rotatableChild;
	}

	private Pane movableChild = null;
	
	public Pane getMovableChild() {
		return movableChild;
	}

	public void setMovableChild(Pane movableChild) {
		
		this.movableChild = movableChild;
		this.movableChild.scaleXProperty().bind(gestureModel.getScaleProperty());
		this.movableChild.scaleYProperty().bind(gestureModel.getScaleProperty());

		this.movableChild.setOnMouseMoved(new EventHandler<MouseEvent>() {
			
			@Override public void handle(MouseEvent event) {
				
				gestureModel.setMouseX(event.getX());
				gestureModel.setMouseY(event.getY());
			  }
	    });
	}
	 
    private SceneGestures sceneGestures = null;
    
    private GestureModel gestureModel = null;
    
    public GestureModel getGestureModel () {
    	
    	return this.gestureModel;
    }
    
    public void setGestureModel(GestureModel gestureModel) {
		this.gestureModel = gestureModel;
	}

	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
        return sceneGestures.getOnMousePressedEventHandler();
    }

    public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
        return sceneGestures.getOnMouseDraggedEventHandler();
    }

    public EventHandler<ScrollEvent> getOnScrollEventHandler() {
        return sceneGestures.getOnScrollEventHandler();
    }
       
	public EventHandler<KeyEvent> getOnKeyRotationPressedEventHandler() {
		return sceneGestures.getOnKeyRotationPressedEventHandler();
	}
	
	public ExtendedCanvas() {
		
		this.sceneGestures = new SceneGestures();
		this.setBackground(new Background(new BackgroundFill(Color.WHITE,new CornerRadii(0.0), new Insets(0.0))));
	
		
//		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
//			
//			@Override public void handle(MouseEvent event) {
//				
//				if(!ExtendedCanvas.this.isFocused()) {
//					ExtendedCanvas.this.requestFocus();
//				}
//			}
//	    });
		
//		this.setOnMouseExited(new EventHandler<MouseEvent>() {
//			
//			@Override public void handle(MouseEvent event) {
//				
//				if(ExtendedCanvas.this.isFocused()) {
//					ExtendedCanvas.this.getScene().getRoot().requestFocus();
//				}
//			}
//	    });
    }

    private class DragContext {

        double mouseAnchorX;
        double mouseAnchorY;

        double translateAnchorX;
        double translateAnchorY;
        
        double mouseScaleX = 0.0;
        double mouseScaleY = 0.0;
    }
    
    private class SceneGestures {

        private static final double maxScale = 15.0d;
        private static final double minScale = 0.00001d;
        //private static final double scaleStep = 0.75;

        private DragContext sceneDragContext = new DragContext();

        public SceneGestures() { 
       
 
        }

        public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
            return onMousePressedEventHandler;
        }

        public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
            return onMouseDraggedEventHandler;
        }

        public EventHandler<ScrollEvent> getOnScrollEventHandler() {
            return onScrollEventHandler;
        }
        
		public EventHandler<KeyEvent> getOnKeyRotationPressedEventHandler() {
			
			return onKeyRotationPressedEventHandler;
		}
		
        private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {

            	ExtendedCanvas.this.requestFocus();
            	
                if(!event.isSecondaryButtonDown()) {
                	
                    return;
                }

                sceneDragContext.mouseAnchorX = event.getSceneX();
                sceneDragContext.mouseAnchorY = event.getSceneY();

                sceneDragContext.translateAnchorX = ExtendedCanvas.this.getMovableChild().getTranslateX();
                sceneDragContext.translateAnchorY = ExtendedCanvas.this.getMovableChild().getTranslateY();
            }

        };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
        	
            public void handle(MouseEvent event) {

            	if(!event.isSecondaryButtonDown()) {
                	
                    return;
                }
            	
            	double xtrans = sceneDragContext.translateAnchorX - sceneDragContext.mouseAnchorX 
            			+ event.getSceneX() + sceneDragContext.mouseScaleX;
                double ytrans = sceneDragContext.translateAnchorY - sceneDragContext.mouseAnchorY 
                		+ event.getSceneY() + sceneDragContext.mouseScaleY;
                
            	ExtendedCanvas.this.getMovableChild().setTranslateX(xtrans);
            	ExtendedCanvas.this.gestureModel.setMouseX(xtrans);
            	ExtendedCanvas.this.getMovableChild().setTranslateY(ytrans);
             	ExtendedCanvas.this.gestureModel.setMouseX(ytrans);
             	
                event.consume();
            }
        };

        private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {

                double scale = ExtendedCanvas.this.gestureModel.getScale(); // currently we only use Y
                double scaleChange = event.getDeltaY() < 0 ? -0.1 : 0.1;
                
                
//                scaleChange *= FastMath.abs(scaleChange * 1.05);
                scale += scaleChange;

                if (scale <= minScale) {
                	
                    scale = minScale;
                } 
                else if (scale >= maxScale) {
                	
                    scale = maxScale;
                }
                else {
                	
	                if(scale > 1) {
	                	
	                	double mouseScaleX = -1 *(ExtendedCanvas.this.gestureModel.getMouseX() -
	                			(ExtendedCanvas.this.getMovableChild().getWidth()/2.0))
	                			* scaleChange;
	                	
	                   	double mouseScaleY = -1 * (ExtendedCanvas.this.gestureModel.getMouseY() -
	                			(ExtendedCanvas.this.getMovableChild().getHeight()/2.0))
	                   			* scaleChange;
	                   	
	                	ExtendedCanvas.this.getMovableChild().setTranslateX(mouseScaleX +
	                	ExtendedCanvas.this.getMovableChild().getTranslateX());
	                	ExtendedCanvas.this.getMovableChild().setTranslateY(mouseScaleY +
	                	ExtendedCanvas.this.getMovableChild().getTranslateY());           
	                }
	                else {
	                	
	                	sceneDragContext.mouseScaleX = 0.0;
	                	sceneDragContext.mouseScaleY = 0.0;
	                }
                }
                
                ExtendedCanvas.this.gestureModel.setScale(scale);
   
                event.consume();
            }
        };
        
        private EventHandler<KeyEvent> onKeyRotationPressedEventHandler = new EventHandler<KeyEvent>() {
        	
			@Override
			public void handle(KeyEvent event) {
				
				if(event.getCode() == KeyCode.UP) {
					
					this.rotate(1.0, Rotate.Z_AXIS);
				}
				else if(event.getCode() == KeyCode.DOWN) {
					
					this.rotate(-1.0, Rotate.Z_AXIS);
				}
				else if(event.getCode() == KeyCode.RIGHT) {
					
					this.rotate(10.0, Rotate.Z_AXIS);
				}
				else if(event.getCode() == KeyCode.LEFT) {
					
					this.rotate(-10.0, Rotate.Z_AXIS);
				}
				else if(event.getCode() == KeyCode.PAGE_UP) {
						
					this.rotate(180.0, Rotate.X_AXIS);
				} 
				else if(event.getCode() == KeyCode.PAGE_DOWN) {
					
					this.rotate(180.0, Rotate.Y_AXIS);
				}
				else if(event.getCode() == KeyCode.I) {
				
					this.clearRotation();
					
					this.rotate(-30.0, Rotate.X_AXIS);
					this.rotate(30.0, Rotate.Z_AXIS);
				}
				else if(event.getCode() == KeyCode.K) {
					
					this.rotate(-10.0, Rotate.Y_AXIS);
				}
				else if(event.getCode() == KeyCode.SPACE) {

					this.clearRotation();

					ExtendedCanvas.this.gestureModel.setScale(1.0);
				}
//				else {
//					
//					fail = true;
//				}
				
//				if(!fail) {
//				
//					event.consume();
//				}
			}
			
			private void clearRotation() {
				
				rotationSave.clear();
				rotation.clear();
				ExtendedCanvas.this.getRotatableChild().getTransforms().clear();					
				ExtendedCanvas.this.getMovableChild().setRotate(0.0);
				ExtendedCanvas.this.getMovableChild().setTranslateX(0.0);
				ExtendedCanvas.this.getMovableChild().setTranslateY(0.0);
			}
			
			private HashMap<Point3D, Double> rotationSave = new HashMap<Point3D, Double>();
			private HashMap<Point3D, Rotate> rotation = new HashMap<Point3D, Rotate>();
			
			private void rotate(double angle, Point3D axis) {
			
				if(!rotationSave.containsKey(axis)) {
					
					rotationSave.put(axis, angle);
					rotation.put(axis, new Rotate(angle, axis));
					rotation.get(axis).setPivotX(ExtendedCanvas.this.getRotatableChild().getWidth()/2.0);
					rotation.get(axis).setPivotY(ExtendedCanvas.this.getRotatableChild().getHeight()/2.0);
					ExtendedCanvas.this.getRotatableChild().getTransforms().add(rotation.get(axis));
				}
				else {
					
					rotationSave.put(axis, rotationSave.get(axis) + angle);
				}
				
 				rotation.get(axis).setAngle(rotationSave.get(axis));
			
			}
		};
    }
}
