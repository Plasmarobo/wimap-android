function Aggrigate
data(1,:,:) = beacon10_fe_ed_b2_22_7c();
data(2,:,:) = beacon10_fe_ed_d1_aa_47();
data(3,:,:) = beaconf8_1a_67_ee_fd_ee();
n = [5,5,5];
powers = [30,20,30];
samples = [2,2,2];
for i = 1:size(data,1)
    figure(i);
    [m, avg] = CollectData(data(i, :, :));
    davg = DistanceFilter(avg, samples(i));
    distances = DistanceModel(powers(i), n(i), 2, avg, 2442);
    fdistances = DistanceModel(powers(i), n(i), 2, davg, 2442);
    plot(1:m, distances, 'r',1:m, 1:m, 'b', 1:m, DistanceFilter(distances, samples(i)), 'g', 1:m, fdistances, 'y');
    
    title('Distance Model')
    xlabel('meters')
    ylabel('Est meters')
end


end