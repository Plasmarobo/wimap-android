require './distance_model.rb'
#require './filter.rb'
require './Filters.rb'
require 'simpleoutput'
require 'simpleplot'
require 'simplechartkick'

include Filters 

graph = SimpleOutput::SimpleOutputEngine.new
plot = SimplePlot.new("_distance")
graph.addPlugin(plot)

model = DistanceModel.new
csv_files = Dir["./*.csv"]
csv_files.each do |file|
	puts file
	power_map = model.parse_powermap_csv(file)
	
	trials = []
	
	min = 100
	
	power_map.each_pair do |(key, row)|
		if row.size < min
			min = row.size
		end
	end
	puts "Min #{min}"
	power_map.each_pair do |(key, row)|
		i = 0
		trials << []
		while(i < min)
			trials.last << row[i].to_f
			i += 1
		end
	end
	x = []
	31.times {|i| x << i}
	raw_average_data = []
	filtered_average_data = []
	min.times do 
		raw_average_data << 0
		filtered_average_data << 0
	end
	index = 0
	trials.map! do |trial|
		puts "Working #{index}/#{trials.size}"
		#filter = Filter.new(7)
		#Update in place
		
		graph.setXY(x,trial, "#{file} Trial #{index} Powermap ", {'series' => 'Raw Data'})
		i = 0
		#trial.map! do |point| 
		#	raw_average_data[i] += point
		#	point = filter.filter(point)
		#	filtered_average_data[i] += point
		#	i += 1
		#	point
		#end
		trial = Filters.moving_average(trial,7)
		graph.appendXY(x, trial, "#{file} Trial #{index} Powermap ", {'series' => 'Filtered Data'})
		index += 1
		trial
	end
	
	#raw_average_data.map! {|x| x / min}
	#filtered_average_data.map! {|x| x / min}
	
	#graph.setXY(x, raw_average_data, "#{file} Powermap Averages", {'series' => 'Raw'})
	#graph.appendXY(x, filtered_average_data, "#{file} Powermap Averages", {'series' => 'Filtered'})

	#Obtain Coeff
	coeff = model.process_trials(trials)
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
			total_point_error[distance] += (guess-distance).abs
		end
	end
	x = []
	31.times do |i|
		x << i
	end
	
	predictions.each_with_index do |y,i| 
		graph.appendXY(x,y,"#{file}DistanceModel", {'series' => "Prediction #{i}", 'xmax' => 30, 'ymin' => 0, 'ymax' => 50, 'xsize' => 3000, 'ysize' => 1000})
	end
	average_error = total_point_error.map {|x| x / trials.size}
	graph.setXY(x, total_point_error, "#{file}Total Error")
	graph.setXY(x, average_error, "#{file}Average Error")
end
graph.save()