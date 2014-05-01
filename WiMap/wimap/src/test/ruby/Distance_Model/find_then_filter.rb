require './distance_model.rb'
require './filter.rb'
require 'simpleoutput'
require 'simpleplot'
require 'simplechartkick'

graph = SimpleOutput::SimpleOutputEngine.new
plot = SimplePlot.new("_distance")
graph.addPlugin(plot)

model = DistanceModel.new
csv_files = Dir["./*.csv"]
csv_files.each do |file|
	power_map = model.parse_powermap_csv(file)
	
	trials = []
	
	min = 100
	
	power_map.each_pair do |(key, row)|
		if row.size < min
			min = row.size
		end
	end
	
	power_map.each_pair do |(key, row)|
		i = 0
		trials << []
		while(i < min)
			trials[i] << row[i]
		end
	end
	#Obtain Coeff
	coeff = model.process_trials(trials)
	x = []
	31.times {|i| x << i}
	raw_average_data = []
	filtered_average_data = []
	trials.each_with_index do |trial|
		puts "Filtering"
		filter = Filter.new(5)
		#Update in place
		raw_average_data << trial
		#graph.setXY(x,trial, "#{file} Trial #{i}", {'series' => 'Raw Data'})
		trail.map! { |point| filter.filter(point)}
		filtered_average_data << trial
		#graph.appendXY(x, trial, "#{file} Trial #{i}", {'series' => 'Filtered Data'})
	end
	
	raw_average_data.transpose.map {|x| x.reduce(:+)}
	filtered_average_data.transpose.map {|x| x.reduce(:+)}
	
	graph.setXY(x, raw_average_data, "#{file} Averages", {'series' => 'Raw'})
	graph.appendXY(x, filtered_average_data, "#{file} Averages", {'series' => 'Filtered'})

	
	predictions = []
	total_point_error = []
	trials.each_with_index do |trial, index|
		predictions << []
		trial.each_with_index do |dBm, distance|
			if(index == 0)
				total_point_error << 0
			end
			guess = model.distance(dBm.to_f,2400.0,coeff) 
			predictions[index] << guess
			total_point_error[distance] += Math.abs(guess-distance)
		end
	end
	x = []
	31.times do |i|
		x << i
	end
	
	min.times do |i|
		graph.appendXY(x,predictions[i],"DistanceModel", {'series' => "Prediction #{i}", 'xsize' => 3000, 'ysize' => 1000})
	end
	average_error = total_point_error.map {|x| x / trials.size}
	graph.setXY(x, total_point_error, "Total Error")
	graph.setXY(x, average_error, "Average Error")
end
graph.save()