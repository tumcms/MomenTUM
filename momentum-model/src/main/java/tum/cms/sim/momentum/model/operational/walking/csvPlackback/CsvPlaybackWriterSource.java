package tum.cms.sim.momentum.model.operational.walking.csvPlackback;

import tum.cms.sim.momentum.model.operational.walking.csvPlackback.CsvPlaybackPedestrianExtensions.CsvPlaybackDataItem;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.ModelPedestrianWriterSource;

public class CsvPlaybackWriterSource extends ModelPedestrianWriterSource<CsvPlaybackOperational, CsvPlaybackPedestrianExtensions> {

	@Override
	protected boolean canWrite(CsvPlaybackPedestrianExtensions currentPedestrianExtension) {

		if(!currentPedestrianExtension.isMemoryReady()) {
			return false;
		}
		
		double currentX = currentPedestrianExtension.getCurrentPosition().getXComponent();
		double currentY = currentPedestrianExtension.getCurrentPosition().getYComponent();
		
		if(currentX < CsvPlaybackPedestrianExtensions.getxMinCut() ||
		   currentX > CsvPlaybackPedestrianExtensions.getxMaxCut() ||
		   currentY < CsvPlaybackPedestrianExtensions.getyMinCut() ||
		   currentY > CsvPlaybackPedestrianExtensions.getyMaxCut()) {
			
			return false;
		}
		
		return true;
	}

//	currentPedestrianExtension.getPerceptItems().forEach(item -> {
//		builder.append(String.format(format, item.getAngleToPercept()));
//		builder.append("|");
//	});
//	builder.deleteCharAt(builder.length() - 1);
//	dataText = builder.toString();
//	break;
	@Override
	protected String getPedestrianData(CsvPlaybackPedestrianExtensions currentPedestrianExtension, String format, String dataElement) {
	
		String dataText = null;
		CsvPlaybackDataItem currentDataItem = null;
		String variant = "last_";
		if(dataElement.startsWith(variant)) {
			currentDataItem = currentPedestrianExtension.getLast();
			dataElement = dataElement.substring(dataElement.indexOf(variant) + variant.length());
		}
		else {
			currentDataItem = currentPedestrianExtension.getCurrent();
		}
		
		StringBuilder builder = new StringBuilder();
		switch(dataElement) {
		
		case "distanceToPercept":
			
			currentDataItem.getPerceptionItems().forEach(item -> {
					builder.append(String.format(format, item.getDistanceToPercept()));
					builder.append("|");
				});
			builder.deleteCharAt(builder.length() - 1);
			dataText = builder.toString();
			break;
			
		case "angleToPercept":
			currentDataItem.getPerceptionItems().forEach(item -> {
				builder.append(String.format(format, item.getAngleToPercept()));
				builder.append("|");
			});
			builder.deleteCharAt(builder.length() - 1);
			dataText = builder.toString();
			break;
			
		case "velocityNormOfPercepts":
			currentDataItem.getPerceptionItems().forEach(item -> {
				builder.append(String.format(format, item.getVelocityMagnitudeOfPercept()));
				builder.append("|");
			});
			builder.deleteCharAt(builder.length() - 1);
			dataText = builder.toString();
			break;
			
		case "angleNormDifferencesToPercepts":
			currentDataItem.getPerceptionItems().forEach(item -> {
				builder.append(String.format(format, item.getVelocityAngleDifferenceToPercept()));
				builder.append("|");
			});
			builder.deleteCharAt(builder.length() - 1);
			dataText = builder.toString();
			break;

		case "typeOfPercept":
			currentDataItem.getPerceptionItems().forEach(item -> {
				builder.append(String.format(format, item.getTypeOfPercept()));
				builder.append("|");
			});
			builder.deleteCharAt(builder.length() - 1);
			dataText = builder.toString();
			break;

		case "angleToGoal":
			dataText =  String.format(format,
					currentDataItem.getGoalItem().getAngleToGoal());
			break;
			
		case "distanceToGoal":
			dataText =  String.format(format,
					currentDataItem.getGoalItem().getDistanceToGoal());
			break;
			
		case "velocityMagnitudeNorm": 
			dataText =  String.format(format,
					currentDataItem.getMovementItem().getVelocityNormValue());
			break;
			
		case "velocityAngleChangeNorm":
			dataText =  String.format(format,
					currentDataItem.getMovementItem().getAngleNormValue());
			break;
			
		case "teachingVelocityMagnitudeClass":
			dataText =  String.format(format,
					currentDataItem.getMovementTeachingItem().getVelocityClassValue());
			break;
			
		case "teachingVelocityAngleChangeClass":
			dataText =  String.format(format,
					currentDataItem.getMovementTeachingItem().getAngleClassValue());
			break;
		default:
			int i = 0;
			break;
		}
		
		return dataText;
	}
}
