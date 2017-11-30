package com.google.androidstudiopoet.models

import com.google.androidstudiopoet.Blueprint
import com.google.androidstudiopoet.utils.joinPath
import com.google.androidstudiopoet.utils.joinPaths

data class AndroidModuleBlueprint(val index: Int,
                                  val numOfActivities: Int,
                                  private val numOfStrings: Int,
                                  private val numOfImages: Int,
                                  val numOfLayouts: Int,
                                  private val projectRoot: String,
                                  val hasLaunchActivity: Boolean,
                                  val useKotlin: Boolean,
                                  val dependencies: List<ModuleDependency>,
                                  val productFlavors: List<Int>?,
                                  private val javaPackageCount: Int, private val javaClassCount: Int, private val javaMethodsPerClass: Int,
                                  private val kotlinPackageCount: Int, private val kotlinClassCount: Int, private val kotlinMethodsPerClass: Int
): Blueprint {

    val name = "androidAppModule" + index
    val packageName = "com.$name"
    val moduleRoot = projectRoot.joinPath(name)
    val srcPath = moduleRoot.joinPath("src")
    val mainPath = srcPath.joinPath("main")
    val resDirPath = mainPath.joinPath("res")
    val codePath = mainPath.joinPath("java")
    val packagePath = codePath.joinPaths(packageName.split("."))

    private val methodsToCallWithIn = dependencies.map { it.methodToCall }

    val packagesBlueprint = PackagesBlueprint(javaPackageCount, javaClassCount, javaMethodsPerClass, kotlinPackageCount,
            kotlinClassCount, kotlinMethodsPerClass, moduleRoot + "/src/main/java/", name, methodsToCallWithIn)

    private val resourcesToReferWithin = dependencies
            .filterIsInstance<AndroidModuleDependency>()
            .map { it.resourcesToRefer }
            .fold(ResourcesToRefer(listOf(), listOf(), listOf())) { acc, resourcesToRefer ->  resourcesToRefer.combine(acc)}

    val resourcesBlueprint = ResourcesBlueprint(name, resDirPath, numOfStrings, numOfImages, numOfLayouts, resourcesToReferWithin)

    val layoutNames = resourcesBlueprint.layoutNames
    val activityNames = 0.until(numOfActivities).map { "Activity$it" }

    var methodToCallFromOutside = packagesBlueprint.methodToCallFromOutside
    var resourcesToReferFromOutside = resourcesBlueprint.resourcesToReferFromOutside
}
