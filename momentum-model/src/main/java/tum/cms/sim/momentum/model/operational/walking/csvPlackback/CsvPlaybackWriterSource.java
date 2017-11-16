package tum.cms.sim.momentum.model.operational.walking.csvPlackback;

import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.ModelPedestrianWriterSource;

public class CsvPlaybackWriterSource extends ModelPedestrianWriterSource<CsvPlaybackOperational, CsvPlaybackPedestrianExtensions> {

	@Override
	protected boolean canWrite(CsvPlaybackPedestrianExtensions currentPedestrianExtension) {

		if(currentPedestrianExtension.isFirstDataSet()) {
			
			currentPedestrianExtension.setFirstDataSet(false);
			
			return false;
		}
		
		return true;
	}

	@Override
	protected String getPedestrianData(CsvPlaybackPedestrianExtensions currentPedestrianExtension, String format, String dataElement) {
	
		String dataText = null;
		StringBuilder builder = new StringBuilder();
				
		switch(dataElement) {
		
		case "perceptionDistance":
			currentPedestrianExtension.getPerceptionDistanceSpace().forEach(distance -> {
				builder.append(String.format(format,distance));
				builder.append("|");
			});
			
			dataText = builder.toString().substring(0, builder.length() - 1);
			break;
			
		case "perceptionVelocityX":
			currentPedestrianExtension.getPerceptionVelocityXSpace().forEach(velocityX -> {
				builder.append(String.format(format,velocityX));
				builder.append("|");
			});
		
		dataText = builder.toString().substring(0, builder.length() - 1);
			break;

		case "perceptionVelocityY":
			currentPedestrianExtension.getPerceptionVelocityYSpace().forEach(velocityY -> {
				builder.append(String.format(format,velocityY));
				builder.append("|");
			});
	
			dataText = builder.toString().substring(0, builder.length() - 1);
			break;

		case "perceptionType":
			currentPedestrianExtension.getPerceptionTypeSpace().forEach(type -> {
				builder.append(String.format(format,type));
				builder.append("|");
			});
	
			dataText = builder.toString().substring(0, builder.length() - 1);
			break;

		case "pedestrianVelocityX":
			dataText = String.format(format,currentPedestrianExtension.getPedestrianVelocityX());
			break;
			
		case "pedestrianVelocityY":
			dataText = String.format(format,currentPedestrianExtension.getPedestrianVelocityY());
			break;
			
		case "pedestrianVelocityXLast":
			dataText = String.format(format,currentPedestrianExtension.getPedestrianVelocityXLast());
			break;
			
		case "pedestrianVelocityYLast":
			dataText = String.format(format,currentPedestrianExtension.getPedestrianVelocityYLast());
			break;
			
		case "pedestrianWalkingGoalX":
			dataText = String.format(format,currentPedestrianExtension.getPedestrianWalkingGoalX());
			break;
			
		case "pedestrianWalkingGoalY":
			dataText = String.format(format,currentPedestrianExtension.getPedestrianWalkingGoalY());
			break;
		}
		
		return dataText;
	}

}
